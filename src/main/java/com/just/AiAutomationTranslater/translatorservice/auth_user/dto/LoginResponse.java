package com.just.AiAutomationTranslater.translatorservice.auth_user.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private long refreshExpiresIn;
    private UserDTO user;
    private List<String> roles;
}