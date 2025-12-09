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

    @KafkaListener(topics = "user-registered",groupId = "user-service-group")
    public void consume(String message){
        log.info("Received USER_REGISTERED event by kafka consumer => : {}",message);

        JSONObject json = new JSONObject(message);

        String username = json.getString("username");
        String email = json.optString("email");

        userService.createProfile(username,email);

        log.info("Profile created automatically for user by kafka event :  {}",username);

    }
}
