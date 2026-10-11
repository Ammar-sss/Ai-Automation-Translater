package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(max = 72, message = "Password must not exceed 72 characters")
    @ToString.Exclude
    private String password;
}