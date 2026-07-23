package com.tushargautamtgs.ride_service.pricing.service;

import com.tushargautamtgs.ride_service.pricing.calculator.DistanceCalculator;
import com.tushargautamtgs.ride_service.pricing.calculator.FareCalculator;
import com.tushargautamtgs.ride_service.pricing.dto.PricingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final DistanceCalculator distanceCalculator;
    private final FareCalculator fareCalculator;

    @Override
    public PricingResponse calculate(
            double pickupLat,
            double pickupLng,
            double dropLat,
            double dropLng
    ) {

        double distance = distanceCalculator.calculate(
                pickupLat,
                pickupLng,
                dropLat,
                dropLng
        );

        int duration = estimateDuration(distance);

        double fare = fareCalculator.calculateFare(
                distance,
                duration
        );

        return PricingResponse.builder()
                .estimatedDistanceKm(round(distance))
                .estimatedDurationMinutes(duration)
                .estimatedFare(round(fare))
                .currency("INR")
                .build();
    }

    private int estimateDuration(double distanceKm) {

        double averageSpeed = 30.0; // km/h (city traffic)

        return (int) Math.ceil((distanceKm / averageSpeed) * 60);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}