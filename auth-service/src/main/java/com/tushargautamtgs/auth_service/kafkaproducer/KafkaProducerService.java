package com.tushargautamtgs.auth_service.kafkaproducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserRegisteredEvent(String username, String role) {

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("role", role);

        String message = json.toString();
        String topic = "user-registered";

        log.info("🔥 Preparing to send USER_REGISTERED event → {}", message);

        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {

                    if (ex == null) {
                        log.info("✅ Successfully sent event to Kafka Topic '{}'", topic);
                        log.info("📌 Partition: {}, Offset: {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    } else {
                        log.error("❌ Failed to send event to Kafka Topic '{}'", topic);
                        log.error("Reason: {}", ex.getMessage());
                    }

                });
    }
}
