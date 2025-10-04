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
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)   // ✅ sign with SecretKey
                .compact();
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
