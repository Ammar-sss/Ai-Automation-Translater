package com.just.AiAutomationTranslater.translatorservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Service for handling JWT token generation and validation.
 */
@Service
public class JwtService {

    private static final String ISSUER = "translator-service";
    private static final String REFRESH_TOKEN_ISSUER = "translator-service-refresh";

    private final SecretKey secretKey;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;
    private final JwtParser accessTokenParser;
    private final JwtParser refreshTokenParser;

    public JwtService(@Value("${jwt.secret}") String base64Secret,
                      @Value("${jwt.access-ttl-minutes:15}") long accessTtlMinutes,
                      @Value("${jwt.refresh-ttl-minutes:43200}") long refreshTtlMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenTtlMs = accessTtlMinutes * 60_000;
        this.refreshTokenTtlMs = refreshTtlMinutes * 60_000;
        this.accessTokenParser = Jwts.parser().verifyWith(secretKey).requireIssuer(ISSUER).build();
        this.refreshTokenParser = Jwts.parser().verifyWith(secretKey).requireIssuer(REFRESH_TOKEN_ISSUER).build();
    }

    /** Creates a signed access token that expires after the configured time. */
    public String generateAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenTtlMs))
                .signWith(secretKey)
                .compact();
    }

    /** Creates a signed refresh token that expires after the configured time.
     *  Adds a random UUID to prevent token collisions.
     */
    public String generateRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuer(REFRESH_TOKEN_ISSUER)
                .issuedAt(now)
                .id(UUID.randomUUID().toString()) // Add random ID to prevent collisions
                .expiration(new Date(now.getTime() + refreshTokenTtlMs))
                .signWith(secretKey)
                .compact();
    }

    /** Reads the email from access token. Throws if the token is expired, tampered, or has the wrong issuer. */
    public String getUsernameFromAccessToken(String token) {
        return parseAccessClaims(token).getSubject();
    }

    /** Reads the email from refresh token. Throws if the token is expired, tampered, or has the wrong issuer. */
    public String getUsernameFromRefreshToken(String token) {
        return parseRefreshClaims(token).getSubject();
    }

    /** The one place where the signature, issuer, and expiry are verified for access token. */
    public Claims parseAccessClaims(String token) {
        return accessTokenParser.parseSignedClaims(token).getPayload();
    }

    /** The one place where the signature, issuer, and expiry are verified for refresh token. */
    public Claims parseRefreshClaims(String token) {
        return refreshTokenParser.parseSignedClaims(token).getPayload();
    }

    // for LoginResponse.expiresIn
    public long getAccessTokenExpirationSeconds() {
        return accessTokenTtlMs / 1000;
    }

    // for LoginResponse.refreshExpiresIn
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenTtlMs / 1000;
    }
}