package com.just.AiAutomationTranslater.translatorservice.security;


import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class AuthUser implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(Role::getRoleName)
                .map(n -> n.startsWith("ROLE_") ? n : "ROLE_" + n)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
