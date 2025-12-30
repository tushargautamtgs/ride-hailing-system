package com.tushargautamtgs.ride_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic rideRequested() {
        return TopicBuilder.name("ride-requested")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rideAssigned() {
        return TopicBuilder.name("ride-assigned")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rideStarted() {
        return TopicBuilder.name("ride-started")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rideCompleted(){
        return TopicBuilder.name("ride-completed")
                .partitions(6)
                .replicas(1)
                .build();
    }
}
