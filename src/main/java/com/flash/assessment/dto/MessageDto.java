package com.flash.assessment.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageDto {

    @NotBlank(message = "The sensitive word cannot be blank or empty")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
