package com.flash.assessment.service;

import com.flash.assessment.component.SensitivityTrie;
import com.flash.assessment.exception.NoDataFoundException;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.repository.SensitiveWordRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanitizerService implements ApplicationRunner {

    private final SensitiveWordRepository sensitiveWordRepository;

    private final SensitivityTrie sensitivityTrie;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        refreshDictionary();
    }

    public SanitizerService(SensitiveWordRepository sensitiveWordRepository, SensitivityTrie sensitivityTrie){
        this.sensitiveWordRepository = sensitiveWordRepository;
        this.sensitivityTrie = sensitivityTrie;
    }

    public void refreshDictionary() {
        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.findAll();
        // Clear and re-populate logic if reloading dynamically
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            sensitivityTrie.insert(sensitiveWord.getWord());
        }
    }

    public String processMessage(String message) {
        if (message.isEmpty()) throw new IllegalArgumentException("Empty message.");
        return sensitivityTrie.sanitize(message, "*");
    }

    public List<SensitiveWord> getAllWords() {
        return sensitiveWordRepository.findAll();
    }

    @Transactional
    public SensitiveWord addWord(SensitiveWord word) {
        if (sensitiveWordRepository.existsByWordIgnoreCase(word.getWord())) {
            throw new IllegalArgumentException("Word already exists in dictionary.");
        }
        SensitiveWord saved = sensitiveWordRepository.save(word);
        refreshDictionary();
        return saved;
    }

    @Transactional
    public SensitiveWord updateWord(Long id, SensitiveWord newWord) {
        SensitiveWord word = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("Word not found"));
        word.setWord(newWord.getWord());
        SensitiveWord updated = sensitiveWordRepository.save(word);
        refreshDictionary();
        return updated;
    }

    @Transactional
    public void deleteWord(Long id) {
        sensitiveWordRepository.deleteById(id);
        refreshDictionary();
    }
}
