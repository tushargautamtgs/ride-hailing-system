package com.tushargautamtgs.ride_service.pricing.service;

import com.tushargautamtgs.ride_service.pricing.dto.PricingResponse;

public interface PricingService {

    PricingResponse calculate(
            double pickupLat,
            double pickupLng,
            double dropLat,
            double dropLng
    );

}