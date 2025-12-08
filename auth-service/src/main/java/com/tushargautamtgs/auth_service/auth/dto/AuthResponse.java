package com.tushargautamtgs.auth_service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class AuthResponse {

    private String accessToken;
    private String refresh;
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken, String refresh) {
        this.accessToken = accessToken;
        this.refresh = refresh;
    }
}
