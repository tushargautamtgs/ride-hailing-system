package com.tushargautamtgs.user_service.security;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    public String extractUsername(String token){
        if (token.startsWith("Bearer "))
            token = token.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }
}
