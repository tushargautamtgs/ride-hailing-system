package com.tushargautamtgs.ride_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRideRequest {

    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;

}
