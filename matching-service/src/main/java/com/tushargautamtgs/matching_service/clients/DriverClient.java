package com.tushargautamtgs.matching_service.clients;


import com.tushargautamtgs.matching_service.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-service",
configuration = FeignAuthConfig.class)
public interface DriverClient {

    @GetMapping("/drivers/{username}/available")
    Boolean isDriverAvailable(@PathVariable("username") String username);
}
