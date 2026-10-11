package com.just.AiAutomationTranslater.translatorservice.auth_user.entity;

import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    @Column(nullable = false)
    @ToString.Exclude
    @NotBlank
    @Size(max = 72) // Bcrypt maximum input length
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    // Fields for account lockout due to failed login attempts
    @Builder.Default
    @Column(nullable = false)
    private int failedAttempts = 0;

    @Builder.Default
    @Column
    private java.time.Instant lockUntil = null;

    @Builder.Default
    @Column
    private java.time.Instant lastLoginAttempt = null;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}