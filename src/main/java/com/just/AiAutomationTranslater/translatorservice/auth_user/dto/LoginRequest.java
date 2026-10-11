package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @ToString.Exclude
    private String password;
}
