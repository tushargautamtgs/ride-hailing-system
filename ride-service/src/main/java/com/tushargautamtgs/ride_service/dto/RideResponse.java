package com.tushargautamtgs.ride_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class RideResponse {

    private UUID rideId;

    private String rider;

    private String assignedDriverUsername;

    private String status;   // CREATED | ASSIGNED | STARTED | COMPLETED

    private Instant validatedAt; // null until ride starts
}
