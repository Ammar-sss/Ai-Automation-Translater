package com.just.AiAutomationTranslater.translatorservice.auth_user.service.impl;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginResponse;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.RegisterRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.RefreshToken;
import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.UserRepository;
import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.RefreshTokenRepository;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.AuthService;
import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.UnauthorizedAccessException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import com.just.AiAutomationTranslater.translatorservice.role.repository.RoleRepository;
import com.just.AiAutomationTranslater.translatorservice.security.JwtService;
import com.just.AiAutomationTranslater.translatorservice.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Validates password strength.
     * Password must contain at least:
     * - 8 characters
     * - 1 uppercase letter
     * - 1 lowercase letter
     * - 1 digit
     * - 1 special character
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    private Response<LoginResponse> buildRefreshTokenAndLoginResponse(String hashedNewRefreshToken, User user, String newRefreshToken, String newAccessToken, String message) {
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(hashedNewRefreshToken)
                .userEmail(user.getEmail())
                .expiryDate(java.time.Instant.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()))
                .build(); // replacedByToken will be set when this token is rotated
        refreshTokenRepository.save(newRefreshTokenEntity);

        // Get user roles
        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        // Convert user to UserDTO
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // Create response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(newAccessToken);
        loginResponse.setRefreshToken(newRefreshToken); // Return the plain token to the client
        loginResponse.setExpiresIn(jwtService.getAccessTokenExpirationSeconds());
        loginResponse.setRefreshExpiresIn(jwtService.getRefreshTokenExpirationSeconds());
        loginResponse.setUser(userDTO); // Populate user field
        loginResponse.setRoles(roles);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(loginResponse)
                .build();
    }

    @Override
    @Transactional(noRollbackFor = UnauthorizedAccessException.class)
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        // Record the login attempt time
        java.time.Instant now = java.time.Instant.now();

        String normalizedEmail = loginRequest.getEmail() != null ? loginRequest.getEmail().trim().toLowerCase(java.util.Locale.ROOT) : null;
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->  new UnauthorizedAccessException("Invalid email or password"));

        // Check if account is locked
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(now)) {
            throw new UnauthorizedAccessException("Invalid email or password");
        }

        // Reset lockout if lock period has expired
        if (user.getLockUntil() != null && !user.getLockUntil().isAfter(now)) {
            user.setLockUntil(null);
            user.setFailedAttempts(0);
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new UnauthorizedAccessException("Invalid email or password");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Increment failed attempts and update last login attempt
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            user.setLastLoginAttempt(now);

            // Lock account after 5 failed attempts
            if (user.getFailedAttempts() >= 5) {
                // Lock for 15 minutes
                user.setLockUntil(now.plus(java.time.Duration.ofMinutes(15)));
            }
            userRepository.save(user);

            throw new UnauthorizedAccessException("Invalid email or password");
        }

        // Successful login - reset failed attempts and lock time
        user.setFailedAttempts(0);
        user.setLockUntil(null);
        user.setLastLoginAttempt(now);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Save refresh token to database for rotation tracking (hashed for security)
        String hashedRefreshToken = TokenHasher.sha256(refreshToken);

        return  buildRefreshTokenAndLoginResponse(hashedRefreshToken, user, refreshToken, accessToken, "Login Successful");
    }

    @Override
    public Response<UserDTO> register(RegisterRequest registerRequest) {


        String normalizedEmail = registerRequest.getEmail() != null ? registerRequest.getEmail().trim().toLowerCase(java.util.Locale.ROOT) : null;
        if(userRepository.findByEmail(normalizedEmail).isPresent()){
            throw new ConflictException("Email already exists");
        }

        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new NotFoundException("Default role User not found"));
        Set<Role> roles = new HashSet<>(Set.of(role));

        // Validate password strength
        validatePasswordStrength(registerRequest.getPassword());

        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                // Initialize account lockout fields for new users
                .failedAttempts(0)
                .lockUntil(null)
                .lastLoginAttempt(null)
                .build();

        userRepository.save(newUser);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(modelMapper.map(newUser, UserDTO.class))
                .build();
    }

    @Override
    public Response<LoginResponse> refreshToken(String refreshToken) {
        try {
            // Hash the incoming token to search for it in the database
            String hashedToken = TokenHasher.sha256(refreshToken);

            // Find the refresh token in the database
            RefreshToken storedToken = refreshTokenRepository.findByToken(hashedToken)
                    .orElseThrow(() -> new UnauthorizedAccessException("Invalid refresh token"));

            // Check if token has been revoked (reuse detection)
            if (storedToken.isRevoked()) {
                // Revoke all tokens for this user as a security measure
                refreshTokenRepository.revokeAllByUserEmail(storedToken.getUserEmail());
                throw new UnauthorizedAccessException("Invalid refresh token");
            }
            if (storedToken.isExpired()) {
                throw new UnauthorizedAccessException("Invalid refresh token");
            }

            // Find the user by email
            User user = userRepository.findByEmail(storedToken.getUserEmail())
                    .orElseThrow(() -> new UnauthorizedAccessException("User not found"));

            // Check if user is enabled
            if (!user.isEnabled()) {
                throw new UnauthorizedAccessException("Account is disabled");
            }

            // Revoke the current refresh token (token rotation)
            storedToken.setRevoked(true);
            storedToken.setRevokedDate(java.time.Instant.now());
            // Track what token replaced this one (store hash of new token)
            String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
            String hashedNewRefreshToken = TokenHasher.sha256(newRefreshToken);
            storedToken.setReplacedByToken(hashedNewRefreshToken);
            refreshTokenRepository.save(storedToken);

            // Generate new access and refresh tokens
            String newAccessToken = jwtService.generateAccessToken(user.getEmail());

            // Save the new refresh token to database (hashed for security)
            return buildRefreshTokenAndLoginResponse(hashedNewRefreshToken, user, newRefreshToken, newAccessToken, "Token refreshed successfully");
        } catch (Exception e) {
            // Token is invalid, expired, or malformed
            return Response.<LoginResponse>builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid or expired refresh token")
                    .build();
        }
    }

    @Override
    public Response<Void> logout(String refreshToken) {
        try {
            // Hash the incoming token to search for it in the database
            String hashedToken = TokenHasher.sha256(refreshToken);

            // Find the refresh token in the database
            RefreshToken storedToken = refreshTokenRepository.findByToken(hashedToken)
                    .orElseThrow(() -> new UnauthorizedAccessException("Invalid refresh token"));

            // Revoke the refresh token
            storedToken.setRevoked(true);
            storedToken.setRevokedDate(java.time.Instant.now());
            refreshTokenRepository.save(storedToken);

            return Response.<Void>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Logged out successfully")
                    .build();
        } catch (Exception e) {
            // Token is invalid or not found
            return Response.<Void>builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid refresh token")
                    .build();
        }
    }
}