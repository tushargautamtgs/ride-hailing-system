package com.tushargautamtgs.user_service.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredConsumer {

    private final UserService userService;

    @KafkaListener(topics = "user-registered", groupId = "user-service-group")
    public void consume(String message) {
        log.info("Received USER_REGISTERED event by kafka consumer => : {}", message);

        JSONObject json = new JSONObject(message);

        String username = json.getString("username");
        String role = json.optString("role");

        if (!role.equalsIgnoreCase("RIDER")) {
            log.info("Skipping the event -> Role is not rider...");
            return;
        }

        userService.createProfile(username);
        log.info("Profile created  <= Kafka event consumed and Processed =>  {}", username);

    }
}
