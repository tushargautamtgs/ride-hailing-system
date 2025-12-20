package com.tushargautamtgs.ride_service.service;

import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;

import java.util.UUID;

public interface RideService {

    RideResponse createRide(String riderUsername, CreateRideRequest request);

    RideResponse getRideById(UUID rideId);

    RideResponse assignDriver(UUID rideId, String driverUsername);

    RideResponse validateRide(UUID rideId, String rideCode);
}
