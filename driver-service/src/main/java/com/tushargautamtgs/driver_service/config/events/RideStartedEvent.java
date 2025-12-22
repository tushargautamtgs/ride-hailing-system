package com.tushargautamtgs.driver_service.config.events;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideStartedEvent {

    private UUID rideId;
    private String driverUsername;
    private Instant startedAt;
}
