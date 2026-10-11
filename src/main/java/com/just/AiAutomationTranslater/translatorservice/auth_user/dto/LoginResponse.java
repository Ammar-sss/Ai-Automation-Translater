package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserDTO user;
    private List<String> roles;
}
