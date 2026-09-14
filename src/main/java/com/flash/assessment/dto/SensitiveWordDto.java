package com.flash.assessment.dto;

import jakarta.validation.constraints.NotBlank;

public record SensitiveWordDto(
        @NotBlank(message = "The sensitive word cannot be blank or empty")
        String word
) {}
