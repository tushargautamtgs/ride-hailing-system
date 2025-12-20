package com.tushargautamtgs.matching_service.clients;


<<<<<<< HEAD
import com.tushargautamtgs.matching_service.config.FeignAuthConfig;
=======
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
import com.tushargautamtgs.matching_service.dto.AssignDriverRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

<<<<<<< HEAD
@FeignClient(
        name = "ride-service",
        configuration = FeignAuthConfig.class
)
=======
@FeignClient(name = "ride-service")
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
public interface RideClient {

    @PostMapping("/rides/{rideId}/assign")
    void assignDriver(
            @PathVariable UUID rideId,
            @RequestBody AssignDriverRequest request
            );

}
