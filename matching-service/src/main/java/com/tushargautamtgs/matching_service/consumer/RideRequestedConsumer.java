package com.tushargautamtgs.matching_service.consumer;

import com.tushargautamtgs.matching_service.event.RideRequestedEvent;
import com.tushargautamtgs.matching_service.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideRequestedConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "ride-requested",
            groupId = "matching-service-group"
    )
    public void consume(RideRequestedEvent event) {

        log.info("🔥 Received ride-requested event | rideId={}", event.getRideId());

        matchingService.match(event);
    }
}

