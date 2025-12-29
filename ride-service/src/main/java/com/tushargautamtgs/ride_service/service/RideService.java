package com.tushargautamtgs.ride_service.service;

import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
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


}
