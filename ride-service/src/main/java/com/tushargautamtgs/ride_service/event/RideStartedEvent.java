package com.tushargautamtgs.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideStartedEvent {
    private UUID rideId;
    private String driverUsername; // or driverId
    private Instant startedAt;
}
