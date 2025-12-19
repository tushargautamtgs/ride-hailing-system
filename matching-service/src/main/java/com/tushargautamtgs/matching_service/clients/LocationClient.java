package com.tushargautamtgs.matching_service.clients;


import com.tushargautamtgs.matching_service.dto.NearbyDriversResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "location-service")
public interface LocationClient {

    @GetMapping("/location/nearby")
    NearbyDriversResponse nearbyDrivers(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusKm
    );

}
