package com.flash.assessment.controller;

import com.flash.assessment.model.SensitiveWord;
import com.flash.assessment.service.SanitizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Sensitive Word Store Management", description = "Endpoints for managing words regarded to be sensitive")
public class AdminController {

    private final SanitizerService sanitizerService;

    public AdminController(SanitizerService sanitizerService){
        this.sanitizerService = sanitizerService;
    }

    @PostMapping("/refresh-dictionary")
    @Operation(summary = "Refresh word cache",
            description = "This refreshes the word cache to get any new words added to the database")
    public ResponseEntity<String> refreshDictionary() {
        sanitizerService.refreshDictionary();
        return ResponseEntity.ok("Dictionary successfully refreshed in memory.");
    }

    @GetMapping("/all-words")
    @Operation(summary = "Get all words",
            description = "Gets all the words currently regarded as sensitive")
    public ResponseEntity<List<SensitiveWord>> getAllWords() {
        return ResponseEntity.status(HttpStatus.OK).body(sanitizerService.getAllWords());
    }

    @PostMapping("/add")
    @Operation(summary = "Add Sensitive word",
            description = "Endpoint to add a new sensitive word to the DB")
    public ResponseEntity<SensitiveWord> addWord(@RequestBody SensitiveWord word) {
        SensitiveWord created = sanitizerService.addWord(word);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a sensitive word",
            description = "Update an existing sensitive word using the id as an identifier")
    public ResponseEntity<SensitiveWord> updateWord(@PathVariable Long id, @RequestBody SensitiveWord word) {
        SensitiveWord updated = sanitizerService.updateWord(id, word);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sensitive word",
            description = "Delete a word from the DB using the id as an identifier")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        sanitizerService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
