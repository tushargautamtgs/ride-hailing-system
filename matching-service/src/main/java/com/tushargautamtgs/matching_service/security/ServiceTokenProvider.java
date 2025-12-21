package com.tushargautamtgs.matching_service.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceTokenProvider {

    private final JwtUtil jwtUtil;
    private String token;

    @PostConstruct
    public void init() {
        this.token = jwtUtil.generateServiceToken("matching-service");

        System.out.println("[MATCHING] Service JWT GENERATED");
        System.out.println("[MATCHING] Token = " + token);
    }


    public String getToken() {
        return token;
    }
}
