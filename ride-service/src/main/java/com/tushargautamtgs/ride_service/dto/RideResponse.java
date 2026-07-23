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

    private String status;

    // Pricing
    private Double estimatedFare;

    private Double estimatedDistanceKm;

    private Integer estimatedDurationMinutes;

    private String currency;

    // Ride Verification
    private String rideCode;

    private Instant validatedAt;

    // Ride Coordinates
    private Double pickupLat;
    private Double pickupLng;

    private Double dropLat;
    private Double dropLng;

    // Ride Time
    private Instant createdAt;
}