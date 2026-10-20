package com.just.AiAutomationTranslater.translatorservice.security;

import com.just.AiAutomationTranslater.translatorservice.exceptions.CustomAccessDenialHandler;
import com.just.AiAutomationTranslater.translatorservice.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableScheduling
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final CustomAccessDenialHandler customAccessDenialHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public static final String[] PUBLIC_POST_PATHS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout"
    };


    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(customAccessDenialHandler))
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(msg -> msg.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .cacheControl(Customizer.withDefaults())
                        // More restrictive CSP for JSON API - no inline scripts or eval
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'none'; script-src 'none'; style-src 'self'; img-src 'self'; font-src 'self'; connect-src 'self'; frame-ancestors 'none'")
                        )
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000) // 1 year in seconds
                        )
                        .referrerPolicy(rp -> rp
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        )
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Stops Spring Boot from also registering AuthFilter as a plain servlet filter (it would run twice)
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Reduced from 14 to 12 for better performance while maintaining security
        // Bcrypt 12 is still secure and much faster than 14
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}