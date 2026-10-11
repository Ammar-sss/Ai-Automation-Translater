package com.just.AiAutomationTranslater.translatorservice.auth_user.repository;

import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken t set t.revoked = true, t.revokedDate = CURRENT_TIMESTAMP where t.userEmail = :email and t.revoked = false")
    void revokeAllByUserEmail(String email);

    @Modifying
    @Query("delete from RefreshToken t where t.expiryDate < :expiryDate")
    int deleteByExpiryDateBefore(Instant expiryDate);
}