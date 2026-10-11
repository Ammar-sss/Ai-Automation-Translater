package com.just.AiAutomationTranslater.translatorservice.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing tokens using SHA-256.
 * Unlike BCrypt, SHA-256 is deterministic - same input always produces same output.
 * This is suitable for token storage where we need to look up tokens by their hash.
 */
public final class TokenHasher {

    private TokenHasher() {
        // Prevent instantiation
    }

    /**
     * Hashes a token using SHA-256.
     *
     * @param token the token to hash
     * @return hexadecimal string representation of the hash
     */
    public static String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Converts byte array to hexadecimal string.
     *
     * @param bytes byte array to convert
     * @return hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}