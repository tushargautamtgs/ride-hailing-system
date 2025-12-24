package com.tushargautamtgs.ride_service.service.impl;

import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.entity.Ride;
import com.tushargautamtgs.ride_service.entity.RideStatus;
import com.tushargautamtgs.ride_service.event.RideAssignedEvent;
import com.tushargautamtgs.ride_service.event.RideRequestedEvent;
import com.tushargautamtgs.ride_service.event.RideStartedEvent;
import com.tushargautamtgs.ride_service.exception.RideNotFoundException;
import com.tushargautamtgs.ride_service.repository.RideRepository;
import com.tushargautamtgs.ride_service.service.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;



@Slf4j
@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {


    private final RideRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Override
    public RideResponse createRide(String riderUsername, CreateRideRequest req) {

        Ride ride = Ride.builder()
                .riderUsername(riderUsername)
                .pickupLat(req.getPickupLat())
                .pickupLng(req.getPickupLng())
                .dropLat(req.getDropLat())
                .dropLng(req.getDropLng())
                .status(RideStatus.REQUESTED)
                .createdAt(Instant.now())
                .build();

        repository.save(ride);

        kafkaTemplate.send(
                "ride-requested",
                ride.getId().toString(),
                new RideRequestedEvent(
                        ride.getId(),
                        ride.getRiderUsername(),
                        ride.getPickupLat(),
                        ride.getPickupLng(),
                        ride.getDropLat(),
                        ride.getDropLng(),
                        ride.getCreatedAt()
                )
        );

        return map(ride);
    }

    @Override
    public RideResponse getRideById(UUID rideId) {

        Ride ride = repository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        return map(ride);
    }



    @Transactional
    @Override
    public RideResponse assignDriver(UUID rideId, String driver) {

        log.info("Assigning driver | rideId={} | driver={}", rideId, driver);
        Ride ride = repository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        String code = generateRideCode();

        ride.setDriverUsername(driver);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setRideCode(code);
        ride.setRideCodeExpiry(Instant.now().plusSeconds(1800)); // 30 minutes expiry

        repository.save(ride);

        kafkaTemplate.send(
                "ride-assigned",
                ride.getId().toString(),
                RideAssignedEvent.builder()
                        .rideId(ride.getId())
                        .riderUsername(ride.getRiderUsername())
                        .driverUsername(driver)
                        .assignedAt(Instant.now())
                        .build()
        );

        log.info("OTP generated for ride assignment => rideId = {} <==> OTP = {}",rideId,code);

        return map(ride);
    }


    @Transactional
    @Override
    public RideResponse validateRide(
            UUID rideId,
            String driverUsername,
            String rideCode
    ) {
        Ride ride = repository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        // 🔐 Only assigned driver allowed
        if (!driverUsername.equals(ride.getDriverUsername())) {

            log.warn(
                    "UnAuthorized ride validation attempt | rideId={} | driverUsername={}",
                    rideId,
                    ride.getDriverUsername(),
                    driverUsername
            );
            throw new AccessDeniedException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Ride can't be started");
        }

        if (ride.getRideCodeExpiry() == null ||
                ride.getRideCodeExpiry().isBefore(Instant.now())) {
            throw new IllegalStateException("OTP expired");
        }

        if (!ride.getRideCode().equals(rideCode)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        //after validation, start the ride

        ride.setStatus(RideStatus.STARTED);
        ride.setRideCode(null);
        ride.setStartedAt(Instant.now());
        ride.setCreatedAt(Instant.now());

        ride.setRideCodeExpiry(null);

        repository.save(ride);

        log.info(
                "Ride STARTED successfully | startedAt={} | rideId={} | driverUsername={}",
                ride.getStartedAt(),
                ride.getId(),
                ride.getDriverUsername()
        );

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(
                                "ride-started",
                                ride.getId().toString(),
                                new RideStartedEvent(
                                        ride.getId(),
                                        ride.getDriverUsername(),
                                        Instant.now()
                                )
                        );

                        log.info(
                                "Kafka EVENT SENT => ride-started | rideId={} | driverUsername={}",
                                ride.getId(),
                                ride.getDriverUsername()
                        );
                    }
                }
        );

        return map(ride);
    }

    private String generateRideCode(){
        return String.valueOf(1000000 + new Random().nextInt(9000000));
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
