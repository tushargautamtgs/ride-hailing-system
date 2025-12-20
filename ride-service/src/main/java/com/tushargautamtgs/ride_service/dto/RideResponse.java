package com.tushargautamtgs.ride_service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class RideResponse {

    private UUID rideId;
    private String rider;
    private String driver;
    private String status;
}
