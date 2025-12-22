package com.tushargautamtgs.driver_service.config.events;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideAssignedEvent {
    private UUID rideId;
    private String riderUsername;
    private String driverUsername;
    private Instant assignedAt;
}
