package com.flash.assessment.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageDto(
        @NotBlank(message = "Message content cannot be blank or empty")
        String message
) {}
