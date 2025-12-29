package com.tushargautamtgs.ride_service.repository;


import com.tushargautamtgs.ride_service.entity.RideState;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RideStateRepository {


    private final RedisTemplate<String, RideState> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(45);


    private String key(UUID rideId){
        return "ride: "+ rideId;
    }

    public void save(RideState state){
        redisTemplate.opsForValue()
                .set(key(state.getRideId()), state,TTL);
    }

    public RideState get(UUID rideId){
        return  redisTemplate.opsForValue().get(key(rideId));
    }

    public void delete(UUID rideId){
        redisTemplate.delete(key(rideId));
    }
}
