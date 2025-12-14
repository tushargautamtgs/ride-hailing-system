package com.tushargautamtgs.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class RideRequestEvent implements Serializable {

    private UUID rideId;

    private String riderUsername;

    private double pickupLat;
    private double pickupLng;

    private double dropLat;
    private double dropLng;

    private Instant requestedAt;

}
