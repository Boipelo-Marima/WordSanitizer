package com.flash.assessment.controller;

import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.service.SanitizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SanitizerService sanitizerService;

    public AdminController(SanitizerService sanitizerService){
        this.sanitizerService = sanitizerService;
    }

    @PostMapping("/refresh-dictionary")
    public ResponseEntity<String> refreshDictionary() {
        sanitizerService.refreshDictionary();
        return ResponseEntity.ok("Dictionary successfully refreshed in memory.");
    }

    @GetMapping("/all-words")
    public ResponseEntity<List<SensitiveWord>> getAllWords() {
        return ResponseEntity.status(HttpStatus.OK).body(sanitizerService.getAllWords());
    }

    @PostMapping("/add")
    public ResponseEntity<SensitiveWord> addWord(@RequestBody SensitiveWord word) {
        SensitiveWord created = sanitizerService.addWord(word);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensitiveWord> updateWord(@PathVariable Long id, @RequestBody SensitiveWord word) {
        SensitiveWord updated = sanitizerService.updateWord(id, word);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        sanitizerService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
