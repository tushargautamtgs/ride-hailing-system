package com.tushargautamtgs.ride_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidateRideRequest {

    @JsonProperty("rideCode")
    private String rideCode;
}
