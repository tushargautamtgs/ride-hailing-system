package com.tushargautamtgs.matching_service.dto;


import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RideRequestedEvent {

    private UUID rideId;
    private double pickupLat;
    private double pickupLng;
}
