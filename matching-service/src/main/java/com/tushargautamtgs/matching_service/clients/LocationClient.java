package com.tushargautamtgs.matching_service.clients;


<<<<<<< HEAD
import com.tushargautamtgs.matching_service.config.FeignAuthConfig;
=======
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
import com.tushargautamtgs.matching_service.dto.NearbyDriversResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
<<<<<<< HEAD
@FeignClient(
        name = "location-service",
        configuration = FeignAuthConfig.class
)
=======

@FeignClient(name = "location-service")
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
public interface LocationClient {

    @GetMapping("/location/nearby")
    NearbyDriversResponse nearbyDrivers(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusKm
    );

}
