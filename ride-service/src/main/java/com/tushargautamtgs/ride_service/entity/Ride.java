package com.tushargautamtgs.ride_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String riderUsername;
    private String driverUsername;

    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private String rideCode;
    private Instant rideCodeExpiry;
    private Instant createdAt;
}
