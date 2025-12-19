package com.tushargautamtgs.matching_service.clients;


import com.tushargautamtgs.matching_service.dto.AssignDriverRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "ride-service")
public interface RideClient {

    @PostMapping("/rides/{rideId}/assign")
    void assignDriver(
            @PathVariable UUID rideId,
            @RequestBody AssignDriverRequest request
            );

}
