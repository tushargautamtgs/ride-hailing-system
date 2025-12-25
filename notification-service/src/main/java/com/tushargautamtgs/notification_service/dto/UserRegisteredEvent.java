package com.tushargautamtgs.notification_service.dto;


import lombok.Data;

@Data
public class UserRegisteredEvent {

    private  String username;
    private  String email;
}
