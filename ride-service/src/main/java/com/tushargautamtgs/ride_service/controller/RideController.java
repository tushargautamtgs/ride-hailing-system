package com.tushargautamtgs.ride_service.controller;


import com.tushargautamtgs.ride_service.dto.AssignDriverRequest;
import com.tushargautamtgs.ride_service.dto.CreateRideRequest;
import com.tushargautamtgs.ride_service.dto.RideResponse;
import com.tushargautamtgs.ride_service.dto.ValidateRideRequest;
import com.tushargautamtgs.ride_service.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {


    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@AuthenticationPrincipal String username, @RequestBody CreateRideRequest request) {
        return ResponseEntity.ok(rideService.createRide(username, request));
    }
//
    @GetMapping("/me/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable UUID rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideForUser(
            @PathVariable UUID rideId,
            Authentication authentication
    ) {
        String username = authentication.getName(); // USER or DRIVER
        return ResponseEntity.ok(
                rideService.getRideForUser(rideId, username)
        );
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


//    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/validate")
    public ResponseEntity<RideResponse> validateRide(
            @PathVariable UUID rideId,
            @RequestBody ValidateRideRequest request,
            Authentication authentication
    ) {

        log.info("➡️ HIT /rides/{}/validate", rideId);

        if (authentication == null) {
            log.error("❌ Authentication is NULL (JWT filter not executed)");
        } else {
            log.info("🔐 Authenticated user = {}", authentication.getName());
            log.info("🔐 Authorities = {}", authentication.getAuthorities());
        }

        if (request == null) {
            log.error("❌ Request body is NULL");
        } else {
            log.info("📩 Incoming OTP = '{}'", request.getRideCode());
        }

        return ResponseEntity.ok(
                rideService.validateRide(
                        rideId,
                        authentication.getName(),
                        request.getRideCode()
                )
        );
    }

    @PostMapping("/{rideId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeRide(
            @PathVariable UUID rideId,
            Authentication authentication
    ) {
        String driverUsername = authentication.getName();
        rideService.completeRide(rideId, driverUsername);
    }

}

