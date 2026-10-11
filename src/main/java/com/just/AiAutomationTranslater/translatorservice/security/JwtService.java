package com.just.AiAutomationTranslater.translatorservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "translator-service";

    private final SecretKey secretKey;
    private final long accessTokenTtlMs;
    private final JwtParser parser;

    public JwtService(@Value("${jwt.secret}") String base64Secret,
                      @Value("${jwt.access-ttl-minutes:15}") long ttlMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenTtlMs = ttlMinutes * 60_000;
        this.parser = Jwts.parser().verifyWith(secretKey).requireIssuer(ISSUER).build();
    }

    /** Creates a signed token that expires after the configured time. */
    public String generateToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenTtlMs))
                .signWith(secretKey)
                .compact();
    }

    /** Reads the email. Throws if the token is expired, tampered, or has the wrong issuer. */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** The expiry is already checked in parseClaims, so only the user match is needed here. */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return getUsernameFromToken(token).equals(userDetails.getUsername());
    }

    /** The one place where the signature, issuer, and expiry are verified. */
    public Claims parseClaims(String token) {          // now public
        return parser.parseSignedClaims(token).getPayload();
    }

    // for LoginResponse.expiresIn
    public long getExpirationSeconds() {
        return accessTokenTtlMs / 1000;
    }
}