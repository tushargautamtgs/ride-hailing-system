package com.tushargautamtgs.auth_service.consumer;

import com.tushargautamtgs.auth_service.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeletedConsumer {

    private final UserRepository repo;

    @Transactional
    @KafkaListener(topics = "user-deleted", groupId = "auth-group")
    public void onUserDeleted(String message) {
        JSONObject json = new JSONObject(message);

        String username = json.getString("username");

        repo.deleteByUsername(username);

        System.out.println("User deleted from system " + username);
    }
}
