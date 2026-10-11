package com.just.AiAutomationTranslater.translatorservice.auth_user.service;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginResponse;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.RegisterRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.res.Response;

public interface AuthService {
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<UserDTO> register(RegisterRequest  registerRequest);
    Response<LoginResponse> refreshToken(String refreshToken);
    Response<Void> logout(String refreshToken);
}
