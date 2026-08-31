package com.flash.assessment.unit;

import com.flash.assessment.component.SensitivityTrie;
import com.flash.assessment.exception.NoDataFoundException;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.repository.SensitiveWordRepository;
import com.flash.assessment.service.SanitizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SanitizerServiceTest {

    @Mock
    private SensitiveWordRepository sensitiveWordRepository;

    @Mock
    private SensitivityTrie sensitivityTrie;

    @InjectMocks
    private SanitizerService sanitizerService;

    private SensitiveWord word;

    @BeforeEach
    void setUp() {
        word = new SensitiveWord();
        word.setId(1L);
        word.setWord("password");
    }

    @Test
    void refreshDictionary_shouldPopulateTrie() {
        when(sensitiveWordRepository.findAll()).thenReturn(List.of(word));

        sanitizerService.refreshDictionary();

        verify(sensitiveWordRepository, times(1)).findAll();
        verify(sensitivityTrie, times(1)).insert("password");
    }

    @Test
    void processMessage_shouldSanitizeText() {
        when(sensitivityTrie.sanitize("test password", "*")).thenReturn("test ********");

        String result = sanitizerService.processMessage("test password");

        assertEquals("test ********", result);
    }

    @Test
    void getAllWords_shouldReturnList() {
        when(sensitiveWordRepository.findAll()).thenReturn(List.of(word));

        List<SensitiveWord> words = sanitizerService.getAllWords();

        assertEquals(1, words.size());
        assertEquals("password", words.get(0).getWord());
    }

    @Test
    void addWord_shouldSaveAndRefresh() {
        when(sensitiveWordRepository.existsByWordIgnoreCase("password")).thenReturn(false);
        when(sensitiveWordRepository.save(any(SensitiveWord.class))).thenReturn(word);
        when(sensitiveWordRepository.findAll()).thenReturn(List.of(word));

        SensitiveWord saved = sanitizerService.addWord(word);

        assertNotNull(saved);
        verify(sensitiveWordRepository).save(word);
        verify(sensitivityTrie).insert("password");
    }

    @Test
    void addWord_shouldThrowExceptionWhenExists() {
        when(sensitiveWordRepository.existsByWordIgnoreCase("password")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> sanitizerService.addWord(word));
        verify(sensitiveWordRepository, never()).save(any());
    }

    @Test
    void updateWord_shouldUpdateAndRefresh() {
        SensitiveWord updatedDetails = new SensitiveWord();
        updatedDetails.setWord("newpass");

        when(sensitiveWordRepository.findById(1L)).thenReturn(Optional.of(word));
        when(sensitiveWordRepository.save(any(SensitiveWord.class))).thenReturn(word);
        when(sensitiveWordRepository.findAll()).thenReturn(List.of(word));

        SensitiveWord result = sanitizerService.updateWord(1L, updatedDetails);

        assertNotNull(result);
        verify(sensitiveWordRepository).save(word);
        verify(sensitivityTrie).insert("newpass");
    }

    @Test
    void updateWord_shouldThrowExceptionWhenNotFound() {
        when(sensitiveWordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoDataFoundException.class, () -> sanitizerService.updateWord(1L, word));
    }

    @Test
    void deleteWord_shouldDeleteAndRefresh() {
        doNothing().when(sensitiveWordRepository).deleteById(1L);
        when(sensitiveWordRepository.findAll()).thenReturn(List.of());

        sanitizerService.deleteWord(1L);

        verify(sensitiveWordRepository).deleteById(1L);
        verify(sensitivityTrie, never()).insert(any());
    }
}