package com.flash.assessment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.assessment.dto.MessageDto;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.model.UserMessage;
import com.flash.assessment.repository.SensitiveWordRepository;
import com.flash.assessment.service.SanitizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class SanitizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper =  new ObjectMapper();

    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;

    @Autowired
    private SanitizerService sanitizerService;

    @BeforeEach
    void setUp() {
        sensitiveWordRepository.deleteAll();

        SensitiveWord word = new SensitiveWord();
        word.setWord("badword");
        sensitiveWordRepository.save(word);

        sanitizerService.refreshDictionary();
    }

    @Test
    void sanitizeMessage_shouldMaskWordsFromDatabase() throws Exception {

        UserMessage request = new UserMessage();
        request.setMessage("This is a badword test");

        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sanitizedMessage").value("This is a ******* test"));
    }

    @Test
    void sanitizeMessage_shouldNotMaskAnyWords() throws Exception {
        UserMessage request = new UserMessage();
        request.setMessage("This is a goodword test.");

        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sanitizedMessage").value("This is a goodword test."));
    }

    @Test
    void sanitizeMessage_withEmptyMessage_shouldHandleGracefully() throws Exception {
        UserMessage request = new UserMessage();
        request.setMessage("");

        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.instance").value("/api/message/sanitize"));
    }

    @Test
    void sanitizeMessage_withMissingPayload_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.instance").value("/api/message/sanitize"));
    }

    @Test
    void sanitizeEndpoint_shouldMaskSensitiveContent() throws Exception {
        SensitiveWord word = new SensitiveWord();
        word.setWord("confidential");
        sensitiveWordRepository.save(word);
        sanitizerService.refreshDictionary();

        MessageDto request = new MessageDto("This is CONFideNtial! data");

        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sanitizedMessage").value("This is ************! data"));
    }
}