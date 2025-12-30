package com.tushargautamtgs.driver_service.config.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideCompletedEvent {
    private UUID rideId;
    private String driverUsername;
    private Instant completedAt;
}
