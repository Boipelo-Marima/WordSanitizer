package com.flash.assessment.controller;

import com.flash.assessment.model.UserMessage;
import com.flash.assessment.service.SanitizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private SanitizerService sanitizationService;

    @PostMapping("/sanitize")
    public ResponseEntity<Map<String, String>> sanitizeMessage(@RequestBody UserMessage request) {
        String sanitizedText = sanitizationService.processMessage(request.getMessage());
        return ResponseEntity.ok(Map.of("original", request.getMessage(), "sanitized", sanitizedText));
    }
}