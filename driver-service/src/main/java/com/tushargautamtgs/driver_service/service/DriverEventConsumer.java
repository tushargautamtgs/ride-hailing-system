package com.tushargautamtgs.driver_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tushargautamtgs.driver_service.config.events.RideAssignedEvent;
import com.tushargautamtgs.driver_service.config.events.RideStartedEvent;
import com.tushargautamtgs.driver_service.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverEventConsumer {

    private final ObjectMapper objectMapper;
    private final DriverService driverService;

    private final DriverStateService driverStateService;


    @KafkaListener(topics = "user-registered", groupId = "driver-service-group")
    public void consumerDriverRegistered(ConsumerRecord<String, String> record) {
        try {
            log.info("=> Received event from Kafka topic='user-registered': {}", record.value());

            String json = record.value();
            UserCreatedEvent event = objectMapper.readValue(json, UserCreatedEvent.class);

            // Log event details
            log.debug(" => Parsed UserCreatedEvent: {}", event);

            // Filter non-driver roles
            if (!"DRIVER".equalsIgnoreCase(event.getRole())) {
                log.info("==> Ignoring event because role={} is not DRIVER", event.getRole());
                return;
            }

            log.info("==> Creating driver profile for username={}", event.getUsername());

            driverService.createIfNotExists(
                    event.getUsername(),
                    event.getEmail()
            );

            log.info("=>> Driver profile created/verified for username={}", event.getUsername());

        } catch (Exception e) {
            log.error("==> Error processing Kafka event in DriverEventConsumer", e);
        }
    }

    @KafkaListener(topics = "ride-assigned", groupId = "driver-service-group")
    public void consumeRideAssigned(ConsumerRecord<String, String> record) {
        try {
            RideAssignedEvent event =
                    objectMapper.readValue(record.value(), RideAssignedEvent.class);

            log.info("Ride ASSIGNED | driver={}", event.getDriverUsername());

            driverStateService.markAssigned(
                    event.getDriverUsername(),
                    event.getRideId().toString()
            );

        } catch (Exception e) {
            log.error("Error processing ride-assigned event", e);
        }
    }

    // 3️⃣ 🔥 RIDE STARTED → ON_RIDE
    @KafkaListener(topics = "ride-started", groupId = "driver-service-group")
    public void consumeRideStarted(ConsumerRecord<String, String> record) {
        try {
            RideStartedEvent event =
                    objectMapper.readValue(record.value(), RideStartedEvent.class);

            log.info("Ride STARTED | driver={} | rideId={}",
                    event.getDriverUsername(), event.getRideId());

            driverStateService.markOnRide(
                    event.getDriverUsername()
            );

        } catch (Exception e) {
            log.error("Error processing ride-started event", e);
        }
    }
}

