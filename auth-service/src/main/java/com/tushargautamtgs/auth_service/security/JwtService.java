package com.tushargautamtgs.auth_service.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private  long expirationMillis;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public  String generateToken(String username, List<String> roles){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+expirationMillis);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("roles",roles))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            getAllClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String extractUsername(String token){
        return getAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token){

        Claims claims = getAllClaims(token);
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list){
            return list.stream().map(String::valueOf).toList();
        }
        return  List.of();
    }

    private Claims getAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
