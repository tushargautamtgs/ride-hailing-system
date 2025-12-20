package com.tushargautamtgs.ride_service.exception;

public class RideNotFoundException extends RuntimeException {

    public RideNotFoundException(String message) {
        super(message);
    }
}
