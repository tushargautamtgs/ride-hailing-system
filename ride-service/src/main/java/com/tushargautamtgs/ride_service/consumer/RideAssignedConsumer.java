package com.tushargautamtgs.ride_service.consumer;

import com.tushargautamtgs.ride_service.entity.Ride;
import com.tushargautamtgs.ride_service.entity.RideStatus;
import com.tushargautamtgs.ride_service.event.RideAssignedEvent;
import com.tushargautamtgs.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideAssignedConsumer {

    private final RideRepository rideRepository;
    @KafkaListener(
            topics = "ride-assigned",
            groupId = "ride-service-group-v2"
    )

    public void handleRideAssigned(RideAssignedEvent event) {

        Ride ride = rideRepository.findById(event.getRideId())
                .orElseThrow();

        ride.setDriverUsername(event.getDriverUsername());
        ride.setStatus(RideStatus.ASSIGNED);

        rideRepository.save(ride);
    }
}
