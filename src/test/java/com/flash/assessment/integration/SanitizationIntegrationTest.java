package com.flash.assessment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
        request.setMessage("This is a badword test.");

        String response = mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

                assertEquals("This is a ******* test.", response);
    }

    @Test
    void sanitizeMessage_shouldNotAnyMaskWords() throws Exception {
        UserMessage request = new UserMessage();
        request.setMessage("This is a goodword test.");

        String response = mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals("This is a goodword test.", response);
    }

    @Test
    void sanitizeMessage_withEmptyMessage_shouldHandleGracefully() throws Exception {
        UserMessage request = new UserMessage();
        request.setMessage("");

        String response = mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        assertEquals("Empty message.", response);
    }

    @Test
    void sanitizeMessage_withMissingPayload_shouldReturnBadRequest() throws Exception {
        String response = mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        assertEquals("Malformed request body", response);
    }
}