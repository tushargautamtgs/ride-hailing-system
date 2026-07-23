package com.tushargautamtgs.ride_service.service;

import com.fasterxml.jackson.databind.node.DoubleNode;
import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.pricing.dto.PricingResponse;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface RideService {

    RideResponse createRide(String riderUsername, CreateRideRequest request);

    RideResponse getRideById(UUID rideId);

    RideResponse assignDriver(UUID rideId, String driverUsername);

    RideResponse getRideForUser(UUID rideId, String username);

    RideResponse validateRide(UUID rideId, String driverUsername, String rideCode);

    /* -------------------------------------------------
           COMPLETE RIDE → DB WRITE (ONLY HERE)
        ------------------------------------------------- */
    /* -------------------------------------------------
           COMPLETE RIDE → DB WRITE (ONLY HERE)
        ------------------------------------------------- */
    @Transactional
    void completeRide(UUID rideId, String driverUsername);

    PricingResponse fetchFare(
            Double pickupLat,
            Double pickupLng,
            Double dropLng,
            Double dropLat
    );


}
