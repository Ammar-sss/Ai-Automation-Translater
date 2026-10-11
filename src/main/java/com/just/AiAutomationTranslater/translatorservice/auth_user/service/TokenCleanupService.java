package com.just.AiAutomationTranslater.translatorservice.auth_user.service;

import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service for cleaning up expired refresh tokens.
 * Runs daily to remove tokens that are no longer needed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Deletes expired refresh tokens from the database.
     * Runs every day at 2:00 AM.
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void deleteExpiredTokens() {
        long deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired refresh tokens", deletedCount);
        }
    }
}