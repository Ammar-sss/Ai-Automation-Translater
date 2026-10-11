package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class RegisterRequest {

    @NotBlank(message = "User name must not be blank")
    @Size(max = 50, message = "User name must be at most 50 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 72,message = "Password must be between 8 and 72 characters")
    @ToString.Exclude
    private String password;
}
