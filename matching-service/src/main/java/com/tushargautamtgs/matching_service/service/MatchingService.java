package com.tushargautamtgs.matching_service.service;

import com.tushargautamtgs.matching_service.clients.LocationClient;
import com.tushargautamtgs.matching_service.clients.RideClient;
import com.tushargautamtgs.matching_service.dto.AssignDriverRequest;
import com.tushargautamtgs.matching_service.dto.NearbyDriversResponse;
import com.tushargautamtgs.matching_service.event.RideRequestedEvent;
import com.tushargautamtgs.matching_service.exception.MatchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private static final double SEARCH_RADIUS_KM = 5.0;
    private static final Duration DRIVER_LOCK_TTL = Duration.ofSeconds(10);

    private final LocationClient locationClient;
    private final RideClient rideClient;
    private final RedisTemplate<String, String> redisTemplate;

    public void match(RideRequestedEvent event) {

        log.info("🚀 Matching started | rideId={}", event.getRideId());

        // 1️⃣ Nearby drivers from Location Service (Redis GEO owner)
        NearbyDriversResponse response =
                locationClient.nearbyDrivers(
                        event.getPickupLat(),
                        event.getPickupLng(),
                        SEARCH_RADIUS_KM
                );

        if (response == null || response.getDriverIds() == null ||
                response.getDriverIds().isEmpty()) {

            throw new MatchingException(
                    "No nearby drivers found for ride " + event.getRideId()
            );
        }

        // 2️⃣ Iterate drivers (priority = nearest order from location-service)
        for (String driverUsername : response.getDriverIds()) {

            String normalized = driverUsername.toLowerCase();


            //checking driver state in redis
            String stateKey = "driver:state:" + normalized;
            String state = redisTemplate.opsForValue().get(stateKey);

            if (!"ONLINE".equals(state)) {

                log.debug(
                        "Skipping driver={} | normalized={} | state={}",
                        driverUsername,
                        normalized,
                        state
                );
                continue;
            }

            // 🔐 3️⃣ Atomic lock (also normalized!)
            String lockKey = "lock:driver:" + normalized;
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(
                            lockKey,
                            event.getRideId().toString(),
                            DRIVER_LOCK_TTL
                    );

            if (!Boolean.TRUE.equals(locked)) {
                log.debug("Driver {} already locked", normalized);
                continue;
            }

            try {
                // 4️⃣ Assign driver (ORIGINAL username goes to Ride Service)
                rideClient.assignDriver(
                        event.getRideId(),
                        new AssignDriverRequest(driverUsername)
                );

                log.info(
                        "✅ Driver {} assigned to ride {}",
                        driverUsername,
                        event.getRideId()
                );
                return;

            } catch (Exception ex) {
                redisTemplate.delete(lockKey);
                log.error("❌ Assignment failed for driver {}", driverUsername, ex);
            }
        }
    }

}

//package com.tushargautamtgs.matching_service.service;
//
//import com.tushargautamtgs.matching_service.clients.DriverClient;
//import com.tushargautamtgs.matching_service.clients.LocationClient;
//import com.tushargautamtgs.matching_service.clients.RideClient;
//import com.tushargautamtgs.matching_service.dto.AssignDriverRequest;
//import com.tushargautamtgs.matching_service.dto.NearbyDriversResponse;
//import com.tushargautamtgs.matching_service.event.RideRequestedEvent;
//import com.tushargautamtgs.matching_service.exception.MatchingException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class MatchingService {
//
//    private static final double SEARCH_RADIUS_KM = 5.0;
//
//    private final LocationClient locationClient;
//    private final DriverClient driverClient;
//    private final RideClient rideClient;
//
//    public void match(RideRequestedEvent event) {
//
//        log.info("==>> Matching started for rideId={}", event.getRideId());
//
//        NearbyDriversResponse response =
//                locationClient.nearbyDrivers(
//                        event.getPickupLat(),
//                        event.getPickupLng(),
//                        SEARCH_RADIUS_KM
//                );
//
//        if (response == null || response.getDriverIds() == null
//                || response.getDriverIds().isEmpty()) {
//
//            throw new MatchingException(
//                    "==>> No nearby drivers found for ride " + event.getRideId()
//            );
//        }
//
//        for (String driverUsername : response.getDriverIds()) {
//
//            Boolean available =
//                    driverClient.isDriverAvailable(driverUsername);
//
//            if (Boolean.TRUE.equals(available)) {
//
//                AssignDriverRequest request =
//                        new AssignDriverRequest(driverUsername);
//
//                rideClient.assignDriver(event.getRideId(), request);
//
//                log.info(
//                        "==>> Driver {} assigned to ride {}  <<==",
//                        driverUsername,
//                        event.getRideId()
//                );
//                return;
//            }
//        }
//
//        throw new MatchingException(
//                " ==>>> No available drivers found for ride " + event.getRideId()
//        );
//    }
//}
