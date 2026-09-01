package com.flash.assessment.controller;

import com.flash.assessment.model.UserMessage;
import com.flash.assessment.service.SanitizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
@Tag(name = "Message sanitization service", description = "Endpoints to receive a message and sanitize sensitive words")
public class MessageController {

    private final SanitizerService sanitizerService;

    public MessageController(SanitizerService sanitizerService){
        this.sanitizerService = sanitizerService;
    }

    @PostMapping("/sanitize")
    @Operation(summary = "Sanitize sensitive words",
            description = "This endpoint receives a message and sanitizes any sensitive words")
    public ResponseEntity<String> sanitizeMessage(@RequestBody UserMessage request) {
        String sanitizedText = sanitizerService.processMessage(request.getMessage());
        return ResponseEntity.ok(sanitizedText);
    }
}