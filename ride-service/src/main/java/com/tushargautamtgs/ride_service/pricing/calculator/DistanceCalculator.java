package com.tushargautamtgs.ride_service.pricing.calculator;


import com.tushargautamtgs.ride_service.pricing.util.HaversineUtil;
import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

    public double calculate(
            double pickupLat,
            double pickupLng,
            double dropLat,
            double dropLng
    ){

        return HaversineUtil.distanceKm(
                pickupLat,
                pickupLng,
                dropLat,
                dropLng
        );
    }
}
