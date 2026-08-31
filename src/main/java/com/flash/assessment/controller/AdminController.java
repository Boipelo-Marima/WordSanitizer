package com.flash.assessment.controller;

import com.flash.assessment.service.SanitizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SanitizerService sanitizerService;

    @PostMapping("/refresh-dictionary")
    public ResponseEntity<String> refreshDictionary() {
        sanitizerService.refreshDictionary();
        return ResponseEntity.ok("Dictionary successfully refreshed in memory.");
    }
}
