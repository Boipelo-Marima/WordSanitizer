package com.flash.assessment.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.assessment.config.CacheConfig;
import com.flash.assessment.controller.MessageController;
import com.flash.assessment.dto.SanitizedMessageDto;
import com.flash.assessment.model.UserMessage;
import com.flash.assessment.service.SanitizerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
@Import(CacheConfig.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SanitizerService sanitizerService;

    @Test
    void sanitizeMessage_shouldReturnSanitizedText() throws Exception {
        UserMessage request = new UserMessage();
        request.setMessage("This is a badword test");

        when(sanitizerService.processMessage("This is a badword test"))
                .thenReturn(new SanitizedMessageDto("This is a ***** test"));

        mockMvc.perform(post("/api/message/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sanitizedMessage").value("This is a ***** test"));
    }
}