package com.tushargautamtgs.ride_service.service.impl;

import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.entity.Ride;
import com.tushargautamtgs.ride_service.entity.RideStatus;
import com.tushargautamtgs.ride_service.event.RideAssignedEvent;
import com.tushargautamtgs.ride_service.event.RideRequestedEvent;
import com.tushargautamtgs.ride_service.exception.RideNotFoundException;
import com.tushargautamtgs.ride_service.repository.RideRepository;
import com.tushargautamtgs.ride_service.service.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

        ride.setDriverUsername(driver);
        ride.setStatus(RideStatus.ASSIGNED);

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

        return map(ride);
    }

    private RideResponse map(Ride ride) {
        return RideResponse.builder()
                .rideId(ride.getId())
                .rider(ride.getRiderUsername())
                .driver(ride.getDriverUsername())
                .status(ride.getStatus().name())
                .build();
    }


}
