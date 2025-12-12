package com.tushargautamtgs.driver_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationResponse {
    private double lat;
    private double lon;
}
