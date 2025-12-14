package com.tushargautamtgs.location_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}