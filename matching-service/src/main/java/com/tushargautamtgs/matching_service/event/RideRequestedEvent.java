package com.tushargautamtgs.matching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestedEvent {

    private UUID rideId;
    private String riderUsername;
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
    private Instant createdAt;
}
