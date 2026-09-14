package com.flash.assessment.service;

import com.flash.assessment.component.SensitivityTrie;
import com.flash.assessment.dto.SanitizedMessageDto;
import com.flash.assessment.dto.SensitiveWordDto;
import com.flash.assessment.event.DictionaryChangedEvent;
import com.flash.assessment.exception.NoDataFoundException;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.repository.SensitiveWordRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class SanitizerService {

    private final SensitiveWordRepository sensitiveWordRepository;
    private final SensitivityTrie sensitivityTrie;
    private final ApplicationEventPublisher eventPublisher;

    public SanitizerService(SensitiveWordRepository sensitiveWordRepository,
                            SensitivityTrie sensitivityTrie,
                            ApplicationEventPublisher eventPublisher){
        this.sensitiveWordRepository = sensitiveWordRepository;
        this.sensitivityTrie = sensitivityTrie;
        this.eventPublisher = eventPublisher;
    }

    public void refreshDictionary() {
        sensitivityTrie.clear();

        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.findAll();
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            sensitivityTrie.insert(sensitiveWord.getWord());
        }
    }

    public SanitizedMessageDto processMessage(String message) {
        if (message.isEmpty()) throw new IllegalArgumentException("Empty message.");
        if(!sensitivityTrie.isPopulated()) refreshDictionary();
        return new SanitizedMessageDto(sensitivityTrie.sanitize(message, "*"));
    }

    @Cacheable(value = "sensitiveWords")
    @Transactional(readOnly = true)
    public List<SensitiveWord> getAllWords() {
        return sensitiveWordRepository.findAll();
    }

    @Transactional
    public SensitiveWord addWord(SensitiveWordDto wordDto) {
        SensitiveWord word = new SensitiveWord();
        word.setWord(wordDto.word().trim().toLowerCase());

        if (sensitiveWordRepository.existsByWordIgnoreCase(word.getWord())) {
            throw new IllegalArgumentException("Word already exists in dictionary.");
        }
        SensitiveWord saved = sensitiveWordRepository.save(word);
        eventPublisher.publishEvent(new DictionaryChangedEvent());
        return saved;
    }

    @Transactional
    public SensitiveWord updateWord(Long id, SensitiveWordDto newWord) {

        SensitiveWord existingWord = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("Word not found"));

        String normalizedWord = newWord.word().trim().toLowerCase();

        if (sensitiveWordRepository.existsByWordIgnoreCase(normalizedWord) &&
                !existingWord.getWord().equals(normalizedWord)) {
            throw new IllegalArgumentException("Word already exists in dictionary.");
        }

        existingWord.setWord(normalizedWord);
        SensitiveWord updated = sensitiveWordRepository.save(existingWord);

        eventPublisher.publishEvent(new DictionaryChangedEvent());
        return updated;
    }

    @Transactional
    public void deleteWord(Long id) {
        if (!sensitiveWordRepository.existsById(id)) {
            throw new NoDataFoundException("Word not found with id: " + id);
        }
        sensitiveWordRepository.deleteById(id);
        eventPublisher.publishEvent(new DictionaryChangedEvent());
    }

    @CacheEvict(value = "sensitiveWords", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDictionaryChangedEvent(DictionaryChangedEvent event) {
        refreshDictionary();
    }
}
