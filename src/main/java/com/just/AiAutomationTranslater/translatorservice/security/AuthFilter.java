package com.just.AiAutomationTranslater.translatorservice.security;


import com.just.AiAutomationTranslater.translatorservice.exceptions.CustomAuthenticationEntryPoint;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserServiceDetails customUserServiceDetails;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.parseAccessClaims(token);              // token is read once
                String email = claims.getSubject();
                if (!StringUtils.hasText(email)) {
                    throw new BadCredentialsException("Token has no subject");
                }

                UserDetails userDetails = customUserServiceDetails.loadUserByUsername(email);
                if (!userDetails.isEnabled()) {                             // finally checks the switch
                    throw new DisabledException("Account disabled");
                }

                var auth = UsernamePasswordAuthenticationToken.authenticated(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
                log.debug("JWT rejected: {}", ex.getClass().getSimpleName());
                SecurityContextHolder.clearContext();
                customAuthenticationEntryPoint.commence(
                        request, response, new BadCredentialsException("Invalid or expired token"));
                return;
            } catch (DataAccessException ex) {
                log.error("Database unavailable while authenticating request", ex);
                SecurityContextHolder.clearContext();
                writeServiceUnavailable(response);
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && Arrays.asList(SecurityConfig.PUBLIC_POST_PATHS).contains(request.getServletPath());
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }
        return null;
    }

    private void writeServiceUnavailable(HttpServletResponse response) throws IOException {
        Response<Void> body = Response.<Void>builder()
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Service temporarily unavailable. Please retry.")
                .build();

        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setHeader("Retry-After", "5");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
