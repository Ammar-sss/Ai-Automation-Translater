package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfile {

    @NotNull(message = "Username is required")
    @Size(max = 50, message = "Username must be at most 50 characters")
    private String username;
}
