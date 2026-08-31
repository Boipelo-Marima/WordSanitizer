package com.flash.assessment.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.assessment.controller.AdminController;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.service.SanitizerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SanitizerService sanitizerService;

    @Test
    void refreshDictionary_shouldReturnOk() throws Exception {
        doNothing().when(sanitizerService).refreshDictionary();

        mockMvc.perform(post("/api/admin/refresh-dictionary"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dictionary successfully refreshed in memory."));
    }

    @Test
    void getAllWords_shouldReturnList() throws Exception {
        SensitiveWord word = new SensitiveWord();
        word.setId(1L);
        word.setWord("test");

        when(sanitizerService.getAllWords()).thenReturn(List.of(word));

        mockMvc.perform(get("/api/admin/all-words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].word").value("test"));
    }

    @Test
    void addWord_shouldReturnCreated() throws Exception {
        SensitiveWord word = new SensitiveWord();
        word.setWord("test");

        SensitiveWord saved = new SensitiveWord();
        saved.setId(1L);
        saved.setWord("test");

        when(sanitizerService.addWord(any(SensitiveWord.class))).thenReturn(saved);

        mockMvc.perform(post("/api/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.word").value("test"));
    }

    @Test
    void updateWord_shouldReturnUpdated() throws Exception {
        SensitiveWord updatedWord = new SensitiveWord();
        updatedWord.setId(1L);
        updatedWord.setWord("updated");

        when(sanitizerService.updateWord(eq(1L), any(SensitiveWord.class))).thenReturn(updatedWord);

        mockMvc.perform(put("/api/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWord)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.word").value("updated"));
    }

    @Test
    void deleteWord_shouldReturnNoContent() throws Exception {
        doNothing().when(sanitizerService).deleteWord(1L);

        mockMvc.perform(delete("/api/admin/1"))
                .andExpect(status().isNoContent());
    }
}