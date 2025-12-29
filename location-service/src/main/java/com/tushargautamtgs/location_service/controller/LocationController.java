package com.tushargautamtgs.location_service.controller;

import com.tushargautamtgs.location_service.dto.LocationUpdateRequest;
import com.tushargautamtgs.location_service.dto.NearbyDriversResponse;
import com.tushargautamtgs.location_service.service.RedisLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final RedisLocationService service;

    // 1️⃣ Driver updates his location
    @PostMapping("/drivers/me")
    public void updateLocation(
            Authentication auth,
            @RequestBody LocationUpdateRequest request
    ) {
        service.updateDriverLocation(
                auth.getName(),
                request.getLatitude(),
                request.getLongitude()
        );
    }

    @PreAuthorize("hasAuthority('SERVICE_MATCHING')")
    @GetMapping("/nearby")
    public NearbyDriversResponse nearbyDrivers(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(name = "radiusKm", defaultValue = "5") double radiusKm,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return new NearbyDriversResponse(
                service.nearbyDrivers(lat, lng, radiusKm, limit)
        );
    }


    // 3️⃣ Driver goes offline
    @DeleteMapping("/drivers/me")
    public void removeDriver(Authentication auth) {
        service.removeDriver(auth.getName());
    }
}
