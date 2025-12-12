package com.tushargautamtgs.driver_service.dto;

import com.tushargautamtgs.driver_service.entity.DriverStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private DriverStatus status;
}
