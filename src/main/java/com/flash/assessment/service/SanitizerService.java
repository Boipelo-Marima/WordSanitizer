package com.flash.assessment.service;

import com.flash.assessment.component.SensitivityTrie;
import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.repository.SensitiveWordRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanitizerService implements ApplicationRunner {

    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;

    @Autowired
    private SensitivityTrie profanityTrie;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        refreshDictionary();
    }

    public void refreshDictionary() {
        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.findAll();
        // Clear and re-populate logic if reloading dynamically
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            System.out.println("word insert:" + sensitiveWord);
            profanityTrie.insert(sensitiveWord.getWord());
        }
    }

    public String processMessage(String message) {
        return profanityTrie.sanitize(message, "*");
    }
}
