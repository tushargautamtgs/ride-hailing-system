package com.tushargautamtgs.ride_service.controller;


import com.tushargautamtgs.ride_service.dto.AssignDriverRequest;
import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.dto.ValidateRideRequest;
import com.tushargautamtgs.ride_service.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {


    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@AuthenticationPrincipal String username, @RequestBody CreateRideRequest request) {
        return ResponseEntity.ok(rideService.createRide(username, request));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable UUID rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }


    @PreAuthorize("permitAll()")
    // for testing purposes
    @PostMapping("/{rideId}/assign")
    public ResponseEntity<RideResponse> assignDriver(
            @PathVariable("rideId") UUID rideId,
            @RequestBody AssignDriverRequest request
    ) {
        return ResponseEntity.ok(
                rideService.assignDriver(rideId, request.getDriverUsername())
        );
    }



    @PostMapping("/{rideId}/validate")
    @PreAuthorize("permitAll")
    public ResponseEntity<RideResponse> validateRide(
            @PathVariable UUID rideId,
            @RequestBody ValidateRideRequest request
            ){
        return ResponseEntity.ok(
                rideService.validateRide(rideId,request.getRideCode())
        );
    }

}

