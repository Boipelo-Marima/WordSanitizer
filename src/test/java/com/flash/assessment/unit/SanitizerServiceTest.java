package com.flash.assessment.unit;

import com.flash.assessment.component.SensitivityTrie;
import com.flash.assessment.config.CacheConfig;
import com.flash.assessment.dto.SensitiveWordDto;
import com.flash.assessment.event.DictionaryChangedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import(CacheConfig.class)
public class SanitizerServiceTest {

    @Mock
    private SensitiveWordRepository sensitiveWordRepository;

    @Mock
    private SensitivityTrie sensitivityTrie;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SanitizerService sanitizerService;

    private SensitiveWord word;

    @BeforeEach
    void setUp() {
        word = new SensitiveWord();
        word.setWord("password");
        word.setId(1L);
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

        String result = sanitizerService.processMessage("test password").sanitizedMessage();

        assertEquals("test ********", result);
    }

    @Test
    void getAllWords_shouldReturnList() {
        when(sensitiveWordRepository.findAll()).thenReturn(List.of(word));

        List<SensitiveWord> words = sanitizerService.getAllWords();

        assertEquals(1, words.size());
        assertEquals("password", words.getFirst().getWord());
    }

    @Test
    void addWord_shouldSaveAndRefresh() {
        when(sensitiveWordRepository.existsByWordIgnoreCase("password")).thenReturn(false);
        when(sensitiveWordRepository.save(any(SensitiveWord.class))).thenReturn(word);

        SensitiveWord saved = sanitizerService.addWord(new SensitiveWordDto(word.getWord()));

        assertNotNull(saved);
        verify(sensitiveWordRepository).save(any(word.getClass()));
        verify(eventPublisher).publishEvent(any(DictionaryChangedEvent.class));
    }

    @Test
    void addWord_shouldThrowExceptionWhenExists() {
        when(sensitiveWordRepository.existsByWordIgnoreCase("password")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> sanitizerService.addWord(new SensitiveWordDto(word.getWord())));
        verify(sensitiveWordRepository, never()).save(any());
    }

    @Test
    void updateWord_shouldUpdateAndRefresh() {
        SensitiveWord updatedDetails = new SensitiveWord();
        updatedDetails.setWord("newpass");

        when(sensitiveWordRepository.findById(1L)).thenReturn(Optional.of(word));
        when(sensitiveWordRepository.save(any(SensitiveWord.class))).thenReturn(word);

        SensitiveWord result = sanitizerService.updateWord(1L, new SensitiveWordDto(updatedDetails.getWord()));

        assertNotNull(result);
        verify(sensitiveWordRepository).save(word);
        verify(eventPublisher).publishEvent(any(DictionaryChangedEvent.class));
    }

    @Test
    void updateWord_shouldThrowExceptionWhenNotFound() {
        when(sensitiveWordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoDataFoundException.class, () -> sanitizerService.updateWord(1L, new SensitiveWordDto(word.getWord())));
    }

    @Test
    void deleteWord_shouldDeleteAndRefresh() {
        when(sensitiveWordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sensitiveWordRepository).deleteById(1L);

        sanitizerService.deleteWord(1L);

        verify(sensitiveWordRepository).deleteById(1L);
        verify(sensitivityTrie, never()).insert(any());
    }
}