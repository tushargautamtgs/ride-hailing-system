package com.tushargautamtgs.matching_service.service;

import com.tushargautamtgs.matching_service.clients.DriverClient;
import com.tushargautamtgs.matching_service.clients.LocationClient;
import com.tushargautamtgs.matching_service.clients.RideClient;
import com.tushargautamtgs.matching_service.dto.AssignDriverRequest;
import com.tushargautamtgs.matching_service.dto.NearbyDriversResponse;
import com.tushargautamtgs.matching_service.event.RideRequestedEvent;
import com.tushargautamtgs.matching_service.exception.MatchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private static final double SEARCH_RADIUS_KM = 5.0;

    private final LocationClient locationClient;
    private final DriverClient driverClient;
    private final RideClient rideClient;

    public void match(RideRequestedEvent event) {

        log.info("🚀 Matching started for rideId={}", event.getRideId());

        NearbyDriversResponse response =
                locationClient.nearbyDrivers(
                        event.getPickupLat(),
                        event.getPickupLng(),
                        SEARCH_RADIUS_KM
                );

        if (response == null || response.getDriverIds() == null
                || response.getDriverIds().isEmpty()) {

            throw new MatchingException(
                    "No nearby drivers found for ride " + event.getRideId()
            );
        }

        for (String driverUsername : response.getDriverIds()) {

            Boolean available =
                    driverClient.isDriverAvailable(driverUsername);

            if (Boolean.TRUE.equals(available)) {

                AssignDriverRequest request =
                        new AssignDriverRequest(driverUsername);

                rideClient.assignDriver(event.getRideId(), request);

                log.info(
                        "✅ Driver {} assigned to ride {}",
                        driverUsername,
                        event.getRideId()
                );
                return;
            }
        }

        throw new MatchingException(
                "No available drivers found for ride " + event.getRideId()
        );
    }
}
