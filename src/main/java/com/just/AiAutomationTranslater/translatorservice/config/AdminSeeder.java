package com.just.AiAutomationTranslater.translatorservice.config;

import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.UserRepository;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import com.just.AiAutomationTranslater.translatorservice.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(adminEmail.isBlank() || adminPassword.isBlank()) return;

        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) return;

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new NotFoundException("Admin role not found"));

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new NotFoundException("USER role not found"));

        User admin = User.builder()
                .username(adminUsername)
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .roles(new HashSet<>(Set.of(adminRole, userRole)))
                .build();

        userRepository.save(admin);
    }
}
