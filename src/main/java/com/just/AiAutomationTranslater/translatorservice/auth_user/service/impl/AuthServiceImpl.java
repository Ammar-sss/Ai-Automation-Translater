package com.just.AiAutomationTranslater.translatorservice.auth_user.service.impl;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.LoginResponse;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.RegisterRequest;
import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.UserRepository;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.AuthService;
import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.UnauthorizedAccessException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import com.just.AiAutomationTranslater.translatorservice.role.repository.RoleRepository;
import com.just.AiAutomationTranslater.translatorservice.security.JwtService;
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
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->  new UnauthorizedAccessException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(roles);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }

    @Override
    public Response<UserDTO> register(RegisterRequest registerRequest) {


        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new ConflictException("Email already exists");
        }

        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new NotFoundException("Default role User not found"));
        Set<Role> roles = new HashSet<>(Set.of(role));

        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(newUser);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(modelMapper.map(newUser, UserDTO.class))
                .build();
    }
}
