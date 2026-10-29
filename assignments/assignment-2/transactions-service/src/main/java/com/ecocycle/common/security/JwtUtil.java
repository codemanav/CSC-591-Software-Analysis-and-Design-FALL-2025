package com.ecocycle.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(String secret, long expiration) {
        // ✅ generate HMAC-SHA key from string
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(Long userId) {
        return buildJwtToken(userId, calculateExpirationDate());
    }

    /**
     * Builds a JWT token with the given subject and expiration.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param userId The user ID to include in the token
     * @param expirationDate The token expiration date
     * @return The compact JWT token string
     */
    private String buildJwtToken(Long userId, Date expirationDate) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * Calculates the expiration date for a JWT token.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @return The expiration date
     */
    private Date calculateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    public Long validateAndExtractUserId(String token) {
        var claims = Jwts.parser()
                .verifyWith(key)   // ✅ now key is SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}
