package com.tushargautamtgs.ride_service.service.impl;

import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.entity.Ride;
import com.tushargautamtgs.ride_service.entity.RideState;
import com.tushargautamtgs.ride_service.entity.RideStatus;
import com.tushargautamtgs.ride_service.event.RideAssignedEvent;
import com.tushargautamtgs.ride_service.event.RideRequestedEvent;
import com.tushargautamtgs.ride_service.event.RideStartedEvent;
import com.tushargautamtgs.ride_service.exception.RideNotFoundException;
import com.tushargautamtgs.ride_service.repository.RideRepository;
import com.tushargautamtgs.ride_service.repository.RideStateRepository;
import com.tushargautamtgs.ride_service.service.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideStateRepository rideStateRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /* -------------------------------------------------
       CREATE RIDE → REDIS ONLY
    ------------------------------------------------- */
    @Override
    public RideResponse createRide(String riderUsername, CreateRideRequest req) {

        UUID rideId = UUID.randomUUID();

        RideState state = RideState.builder()
                .rideId(rideId)
                .riderUsername(riderUsername)
                .pickupLat(req.getPickupLat())
                .pickupLng(req.getPickupLng())
                .dropLat(req.getDropLat())
                .dropLng(req.getDropLng())
                .status(RideStatus.REQUESTED)
                .createdAt(Instant.now())
                .build();

        rideStateRepository.save(state);

        kafkaTemplate.send(
                "ride-requested",
                rideId.toString(),
                new RideRequestedEvent(
                        rideId,
                        riderUsername,
                        req.getPickupLat(),
                        req.getPickupLng(),
                        req.getDropLat(),
                        req.getDropLng(),
                        state.getCreatedAt()
                )
        );

        log.info("Kafka event published: ride-requested | rideId={} | rider={}", rideId, riderUsername);

        return map(state);
    }

    /* -------------------------------------------------
       GET RIDE → REDIS FIRST, DB FALLBACK
    ------------------------------------------------- */
    @Override
    public RideResponse getRideById(UUID rideId) {

        RideState state = rideStateRepository.get(rideId);
        if (state != null) {
            return map(state);
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        return map(ride);
    }

    /* -------------------------------------------------
       ASSIGN DRIVER → REDIS ONLY
    ------------------------------------------------- */
    @Override
    public RideResponse assignDriver(UUID rideId, String driverUsername) {

        RideState ride = rideStateRepository.get(rideId);
        if (ride == null) {
            throw new RideNotFoundException("Ride not found");
        }

        String otp = generateRideCode();

        ride.setDriverUsername(driverUsername);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setRideCode(otp);
        ride.setRideCodeExpiry(Instant.now().plusSeconds(1800));

        rideStateRepository.save(ride);

        kafkaTemplate.send(
                "ride-assigned",
                rideId.toString(),
                RideAssignedEvent.builder()
                        .rideId(rideId)
                        .riderUsername(ride.getRiderUsername())
                        .driverUsername(driverUsername)
                        .assignedAt(Instant.now())
                        .build()
        );

        log.info("OTP generated | rideId={} | otp={}", rideId, otp);

        return map(ride);
    }

    @Override
    public RideResponse getRideForUser(UUID rideId, String username) {

        RideState ride = rideStateRepository.get(rideId);
        if (ride == null) {
            throw new RideNotFoundException("Ride not found");
        }

        // 🔐 OWNERSHIP CHECK
        boolean isRider = username.equals(ride.getRiderUsername());
        boolean isDriver = username.equals(ride.getDriverUsername());

        if (!isRider && !isDriver) {
            throw new AccessDeniedException("Not allowed to view this ride");
        }

        return RideResponse.builder()
                .rideId(ride.getRideId())
                .status(ride.getStatus().name())
                .assignedDriverUsername(ride.getDriverUsername())
                .rideCode(ride.getRideCode()) // 👈 USER ko OTP milega
                .build();
    }


    /* -------------------------------------------------
       VALIDATE RIDE (OTP) → REDIS ONLY
    ------------------------------------------------- */
    @Override
    public RideResponse validateRide(UUID rideId,
                                     String driverUsername,
                                     String rideCode) {

// for tetsing only
        log.info("====== VALIDATE called ======");
        log.info("rideId = {}", rideId);
        log.info("driverUsername(from JWT) = {}", driverUsername);


        RideState ride = rideStateRepository.get(rideId);
        if (ride == null) {
            throw new RideNotFoundException("Ride not found");
        }

        if (!driverUsername.equals(ride.getDriverUsername())) {
            throw new AccessDeniedException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Ride cannot be started");
        }

        log.info("⏰ NOW = {}", Instant.now());
        log.info("⏰ OTP EXPIRY = {}", ride.getRideCodeExpiry());


        if (ride.getRideCodeExpiry() == null ||
                ride.getRideCodeExpiry().isBefore(Instant.now())) {
            throw new IllegalStateException("OTP expired");
        }

        if (!ride.getRideCode().equals(rideCode)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(Instant.now());
        ride.setRideCode(null);
        ride.setRideCodeExpiry(null);

        rideStateRepository.save(ride);

        kafkaTemplate.send(
                "ride-started",
                rideId.toString(),
                new RideStartedEvent(
                        rideId,
                        driverUsername,
                        ride.getStartedAt()
                )
        );

        log.info("Ride STARTED | rideId={} | driver={}", rideId, driverUsername);

        return map(ride);
    }

    /* -------------------------------------------------
       COMPLETE RIDE → DB WRITE (ONLY HERE)
    ------------------------------------------------- */
    @Transactional
    @Override
    public void completeRide(UUID rideId, String driverUsername) {

        RideState state = rideStateRepository.get(rideId);
        if (state == null) {
            throw new RideNotFoundException("Ride not found in active state");
        }

        // ✅ 1. DRIVER OWNERSHIP CHECK (MANDATORY)
        if (!driverUsername.equals(state.getDriverUsername())) {
            throw new AccessDeniedException("Driver not allowed to complete this ride");
        }

        // ✅ 2. STATE VALIDATION (FSM RULE)
        if (state.getStatus() != RideStatus.STARTED) {
            throw new IllegalStateException(
                    "Ride can be completed only after STARTED state"
            );
        }

        // ✅ 3. REDIS → DB FINAL SNAPSHOT
        Ride ride = Ride.builder()
                .id(state.getRideId())
                .riderUsername(state.getRiderUsername())
                .driverUsername(state.getDriverUsername())
                .pickupLat(state.getPickupLat())
                .pickupLng(state.getPickupLng())
                .dropLat(state.getDropLat())
                .dropLng(state.getDropLng())
                .status(RideStatus.COMPLETED)
                .createdAt(state.getCreatedAt())
                .startedAt(state.getStartedAt())
                .completedAt(Instant.now())
                .build();

        rideRepository.save(ride);          // ✅ SINGLE DB WRITE
        rideStateRepository.delete(rideId); // ✅ REDIS CLEANUP
    }


    /* -------------------------------------------------
       UTILS
    ------------------------------------------------- */
    private String generateRideCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private RideResponse map(RideState ride) {
        return RideResponse.builder()
                .rideId(ride.getRideId())
                .rider(ride.getRiderUsername())
                .assignedDriverUsername(ride.getDriverUsername())
                .status(ride.getStatus().name())
                .validatedAt(ride.getStartedAt())
                .build();
    }

    private RideResponse map(Ride ride) {
        return RideResponse.builder()
                .rideId(ride.getId())
                .rider(ride.getRiderUsername())
                .assignedDriverUsername(ride.getDriverUsername())
                .status(ride.getStatus().name())
                .validatedAt(ride.getStartedAt())
                .build();
    }
}
