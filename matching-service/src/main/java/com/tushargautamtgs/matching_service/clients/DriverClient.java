package com.tushargautamtgs.matching_service.clients;


<<<<<<< HEAD
import com.tushargautamtgs.matching_service.config.FeignAuthConfig;
=======
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

<<<<<<< HEAD
@FeignClient(name = "driver-service",
configuration = FeignAuthConfig.class)

=======
@FeignClient(name = "driver-service")
>>>>>>> 5bfced7f705fcbbc59608da46c9e38eb238e0c16
public interface DriverClient {

    @GetMapping("/drivers/{username}/available")
    Boolean isDriverAvailable(@PathVariable("username") String username);
}
