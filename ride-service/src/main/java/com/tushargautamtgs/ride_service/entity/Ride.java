package com.tushargautamtgs.ride_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    private UUID id;

    private String riderUsername;
    private String driverUsername;

    private Double pickupLat;
    private Double pickupLng;

    private Double dropLat;
    private Double dropLng;

    // ---------------- Pricing ----------------

    private Double estimatedDistanceKm;

    private Integer estimatedDurationMinutes;

    private Double estimatedFare;

    @Builder.Default
    private String currency = "INR";

    // -----------------------------------------

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;
}