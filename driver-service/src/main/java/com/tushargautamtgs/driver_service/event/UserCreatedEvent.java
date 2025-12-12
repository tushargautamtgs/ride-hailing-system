package com.tushargautamtgs.driver_service.event;

import lombok.Data;

@Data
public class UserCreatedEvent {
    private String username;
    private String role; // ADD THIS
    private String name;
    private String phone;
    private String vehicleNumber;
    private String vehicleType;
}
