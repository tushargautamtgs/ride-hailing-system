package com.tushargautamtgs.ride_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideState {

    private UUID rideId;

    private String riderUsername;

    private String driverUsername;

    private double pickupLat;
    private double pickupLng;

    private double dropLat;
    private double dropLng;

    // ---------------- Pricing ----------------

    private Double estimatedDistanceKm;

    private Integer estimatedDurationMinutes;

    private Double estimatedFare;

    @Builder.Default
    private String currency = "INR";

    // -----------------------------------------

    private RideStatus status;

    private String rideCode;

    private Instant rideCodeExpiry;

    private Instant createdAt;

    private Instant startedAt;
}