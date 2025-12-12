package com.tushargautamtgs.driver_service.dto;


import com.tushargautamtgs.driver_service.entity.DriverStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfileResponse {

    private Long Id;
    private String username;
    private String name;
    private String phone;
    private String vehicleNumber;
    private String vehicleType;
    private boolean active;
    private DriverStatus status;
}
