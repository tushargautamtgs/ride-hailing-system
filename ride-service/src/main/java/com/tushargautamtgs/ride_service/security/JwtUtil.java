package com.tushargautamtgs.ride_service.security;

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

    public JwtUtil(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }




    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object roles = getAllClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
    private Claims getAllClaims(String token) {
        token = cleanToken(token);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private String cleanToken(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Missing JWT token");
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token.trim();
    }
}
