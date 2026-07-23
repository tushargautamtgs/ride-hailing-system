package com.tushargautamtgs.ride_service.pricing.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PricingResponse {

    private Double estimatedDistanceKm;

    private Integer estimatedDurationMinutes;

    private Double estimatedFare;

    private String currency;

}