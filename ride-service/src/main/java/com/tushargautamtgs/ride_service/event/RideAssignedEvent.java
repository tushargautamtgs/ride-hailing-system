package com.tushargautamtgs.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideAssignedEvent implements Serializable {

    private UUID rideId;

    private String riderUsername;
    private String driverUsername;

    private Instant assignedAt;
}
