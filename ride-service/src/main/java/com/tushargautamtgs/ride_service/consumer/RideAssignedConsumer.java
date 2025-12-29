//package com.tushargautamtgs.ride_service.consumer;
//
//import com.tushargautamtgs.ride_service.entity.Ride;
//import com.tushargautamtgs.ride_service.entity.RideStatus;
//import com.tushargautamtgs.ride_service.event.RideAssignedEvent;
//import com.tushargautamtgs.ride_service.repository.RideRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class RideAssignedConsumer {
//
//    private final RideRepository rideRepository;
//    @KafkaListener(topics = "ride-assigned", groupId = "ride-service-group-v2")
//    public void handleRideAssigned(RideAssignedEvent event) {
//
//        Optional<Ride> optionalRide =
//                rideRepository.findById(event.getRideId());
//
//        if (optionalRide.isEmpty()) {
//            log.warn(
//                    "Ride not found for ride-assigned event. Skipping. rideId={}",
//                    event.getRideId()
//            );
//            return; // ✅ VERY IMPORTANT
//        }
//
//        Ride ride = optionalRide.get();
//
//        // optional: idempotency check
//        if (ride.getStatus() == RideStatus.ASSIGNED) {
//            log.info("Ride already assigned, skipping duplicate event");
//            return;
//        }
//
//        // normal processing
//    }
