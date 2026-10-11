package com.just.AiAutomationTranslater.translatorservice.auth_user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // We'll store the hashed token for security

    @Column(nullable = false)
    private String userEmail; // Email of the user this token belongs to

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(nullable = true)
    private Instant revokedDate;

    @Column(nullable = true)
    private String replacedByToken; // Token that replaced this one (for rotation tracking)

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }
}