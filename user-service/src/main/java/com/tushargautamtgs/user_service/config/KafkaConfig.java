package com.tushargautamtgs.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name("user-deleted")
                .partitions(1) // only for testing purpose
                .replicas(1)
                .build();
    }
}
