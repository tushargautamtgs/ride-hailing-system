package com.tushargautamtgs.driver_service.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDriverRequest {

    private String name;
    private String phone;
    private String vehicleNumber;
    private String vehicleType;
}
