package com.just.AiAutomationTranslater.translatorservice.auth_user.controller;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginResponse;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.RegisterRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.RefreshTokenRequest;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Response<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Response<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Response<UserDTO> response = authService.register(registerRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        Response<LoginResponse> response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        Response<Void> response = authService.logout(request.getRefreshToken());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}