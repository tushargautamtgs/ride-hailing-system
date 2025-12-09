package com.tushargautamtgs.user_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponse {

    private String username;
    private String fullName;
    private String email;
    private String phone;
}
