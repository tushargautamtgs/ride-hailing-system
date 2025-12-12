package com.tushargautamtgs.driver_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration:3600000}") long expirationMillis
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    private String cleanToken(String token) {
        if (token == null) return null;
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }

    public Jws<Claims> parseToken(String token) {
        token = cleanToken(token);
        if (token == null || token.isBlank()) {
            throw new JwtException("Empty JWT");
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String extractUsername(String token) {
        try {
            return parseToken(token).getBody().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Extract roles claim (expected to be a List in the token). Returns empty list on any issue.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Object rolesObj = parseToken(token).getBody().get("roles");
            if (rolesObj instanceof List<?> list) {
                return list.stream().map(String::valueOf).toList();
            }
        } catch (JwtException ignored) { }
        return List.of();
    }

    public boolean validate(String token) {
        try {
            parseToken(token); // will throw if invalid/expired
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
