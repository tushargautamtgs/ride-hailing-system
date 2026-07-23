package com.tushargautamtgs.ride_service.pricing.calculator;


import org.bouncycastle.pqc.crypto.newhope.NHSecretKeyProcessor;
import org.springframework.stereotype.Component;

@Component
public class FareCalculator {

    private static final double BASE_FARE = 49;

    private static final double PER_KM = 15;

    private static final double PER_MINUTE = 2;

    public double calculateFare(
            double distanceKm,
            int dusrationMinutes
    ){
        return BASE_FARE
                +(distanceKm * PER_KM)
                +(dusrationMinutes * PER_MINUTE);
    }

}
