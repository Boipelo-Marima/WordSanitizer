package com.flash.assessment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.assessment.dto.SensitiveWordDto;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.repository.SensitiveWordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;

    @BeforeEach
    void setUp() {
        sensitiveWordRepository.deleteAll();
    }

    @Test
    void getAllWords_shouldReturnList() throws Exception {
        SensitiveWord word = new SensitiveWord();
        word.setWord("spam");
        sensitiveWordRepository.save(word);

        mockMvc.perform(get("/api/admin/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].word").value("spam"));
    }

    @Test
    void addWord_shouldCreateAndReturnWord() throws Exception {
        SensitiveWordDto newWord = new SensitiveWordDto("malicious");

        mockMvc.perform(post("/api/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.word").value("malicious"));
    }

    @Test
    void updateWord_shouldModifyExistingWord() throws Exception {
        SensitiveWord saved = new SensitiveWord();
        saved.setWord("oldword");
        saved = sensitiveWordRepository.save(saved);

        SensitiveWordDto updateDto = new SensitiveWordDto("updatedword");

        mockMvc.perform(put("/api/admin/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("updatedword"));
    }

    @Test
    void deleteWord_shouldRemoveWord() throws Exception {
        SensitiveWord saved = new SensitiveWord();
        saved.setWord("deleteme");
        saved = sensitiveWordRepository.save(saved);

        mockMvc.perform(delete("/api/admin/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void refreshDictionary_shouldSucceed() throws Exception {
        mockMvc.perform(post("/api/admin/refresh"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dictionary successfully refreshed in memory."));
    }

    @Test
    void addDuplicateWord_shouldThrowException() throws Exception {
        SensitiveWord word = new SensitiveWord();
        word.setWord("duplicate");
        sensitiveWordRepository.save(word);

        SensitiveWordDto duplicate = new SensitiveWordDto("duplicate");

        mockMvc.perform(post("/api/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateNonExistentWord_shouldFail() throws Exception {
        SensitiveWordDto word = new SensitiveWordDto("missing");

        mockMvc.perform(put("/api/admin/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isNotFound());
    }
}