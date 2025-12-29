package com.tushargautamtgs.notification_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tushargautamtgs.notification_service.dto.UserRegisteredEvent;
import com.tushargautamtgs.notification_service.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(
            topics = "user-registered",
            groupId = "notification-service-group"
    )
    public void consume(ConsumerRecord<String, String> record) {

        try {
            UserRegisteredEvent event =
                    objectMapper.readValue(record.value(), UserRegisteredEvent.class);

            log.info("==>> EVENT PICKED | username={} | email={}",
                    event.getUsername(),
                    event.getEmail()
            );

            emailService.sendWelcomeEmail(
                    event.getEmail(),
                    event.getUsername()
            );

            log.info("==>> WAITING 2 seconds before next event...");
            Thread.sleep(2000);
            log.info("▶READY for next event");

        } catch (Exception e) {

            log.error("==>> Error while processing record={}, skipping.",
                    record.value(), e);
        }
    }
}
