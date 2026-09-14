package com.flash.assessment.controller;

import com.flash.assessment.dto.MessageDto;
import com.flash.assessment.dto.SanitizedMessageDto;
import com.flash.assessment.service.SanitizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<SanitizedMessageDto> sanitizeMessage(@Valid @RequestBody MessageDto request) {
        SanitizedMessageDto sanitizedText = sanitizerService.processMessage(request.message());
        return ResponseEntity.ok(sanitizedText);
    }
}