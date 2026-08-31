package com.flash.assessment.controller;

import com.flash.assessment.model.UserMessage;
import com.flash.assessment.service.SanitizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final SanitizerService sanitizerService;

    public MessageController(SanitizerService sanitizerService){
        this.sanitizerService = sanitizerService;
    }

    @PostMapping("/sanitize")
    public ResponseEntity<String> sanitizeMessage(@RequestBody UserMessage request) {
        String sanitizedText = sanitizerService.processMessage(request.getMessage());
        return ResponseEntity.ok(sanitizedText);
    }
}