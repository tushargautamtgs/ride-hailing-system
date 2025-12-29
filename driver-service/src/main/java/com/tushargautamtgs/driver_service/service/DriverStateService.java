package com.tushargautamtgs.driver_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.springframework.data.redis.core.RedisCommand.TTL;

@Service
public class DriverStateService {

    private final RedisTemplate<String, String> redis;

    public DriverStateService(
            @Qualifier("redisTemplate")
            RedisTemplate<String, String> redis
    ) {
        this.redis = redis;
    }

    private String key(String driver) {
        return "driver:state:" + driver.toLowerCase();
    }

    private String rideKey(String driver) {
        return "driver:ride:" + driver.toLowerCase();
    }

    public void markOnline(String driver) {
        redis.opsForValue().set(key(driver), "ONLINE", 45, TimeUnit.MINUTES);
    }

    public void markAssigned(String driver, String rideId) {
        redis.opsForValue().set(key(driver), "ASSIGNED", 45, TimeUnit.MINUTES);
        redis.opsForValue().set(rideKey(driver), rideId, 45, TimeUnit.MINUTES);
    }

    public void markOnRide(String driver) {
        redis.opsForValue().set(key(driver), "ON_RIDE", 45, TimeUnit.MINUTES);
    }

    public void markOffline(String driver) {
        String state = redis.opsForValue().get(key(driver));
        if ("ON_RIDE".equals(state)) {
            throw new IllegalStateException("Driver cannot go OFFLINE during ride");
        }
        redis.delete(key(driver));
        redis.delete(rideKey(driver));
    }

    public boolean isOnline(String driver) {
        return "ONLINE".equals(redis.opsForValue().get(key(driver)));
    }
}
