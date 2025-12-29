package com.tushargautamtgs.driver_service.controller;


import com.tushargautamtgs.driver_service.dto.*;
import com.tushargautamtgs.driver_service.entity.DriverProfile;
import com.tushargautamtgs.driver_service.entity.DriverStatus;
import com.tushargautamtgs.driver_service.service.DriverService;
import com.tushargautamtgs.driver_service.service.DriverStateService;
import com.tushargautamtgs.driver_service.service.RedisLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    private final RedisLocationService locationService;

    private final DriverStateService  driverStateService;

    @GetMapping("/me")
    public ResponseEntity<DriverProfileResponse> getmyProfile(@AuthenticationPrincipal String username){
        DriverProfile p = driverService.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Profile Not Found"));
        return ResponseEntity.ok(toResponse(p));
    }

    @PutMapping("/me")
    public ResponseEntity<DriverProfileResponse> updateMyProfile(@AuthenticationPrincipal String username,
                                                                 @RequestBody UpdateDriverRequest req){
        DriverProfile updated = driverService.updateProfile(username,req);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PutMapping("/me/online")
    public ResponseEntity<Void> goOnline(@AuthenticationPrincipal String username) {
        driverStateService.markOnline(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/offline")
    public ResponseEntity<Void> goOffline(@AuthenticationPrincipal String username) {
        driverStateService.markOffline(username);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/me/location")
    public ResponseEntity<?> updateLocation(
            @AuthenticationPrincipal String username,
            @RequestBody LocationUpdateRequest req
    ) {
        locationService.updateDriverLocation(username, req.getLat(), req.getLon());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/location")
    public ResponseEntity<?> getMyLocation(@AuthenticationPrincipal String username) {

        Point point = locationService.getDriverLocation(username);
        if (point == null) {
            return ResponseEntity.status(404).body("Location not available");
        }

        return ResponseEntity.ok(
                new LocationResponse(point.getY(), point.getX()) // lat = Y, lon = X
        );
    }

    @GetMapping("/{username}/available")
    public Boolean isDriverAvailable(@PathVariable String username) {
        return driverStateService.isOnline(username);
    }



    private DriverProfileResponse toResponse(DriverProfile p){
        return DriverProfileResponse.builder()
                .Id(p.getId())
                .username(p.getUsername())
                .name(p.getName())
                .phone(p.getPhone())
                .email(p.getEmail())
                .vehicleNumber(p.getVehichleNumber())
                .vehicleType(p.getVehicleType())
                .active(p.isActive())
                .status(p.getStatus())
                .build();
    }


}
