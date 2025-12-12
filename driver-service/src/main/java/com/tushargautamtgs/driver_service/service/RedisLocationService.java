package com.tushargautamtgs.driver_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLocationService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY = "drivers:locations";

    public void updateDriverLocation(String driverId, double lat, double lon) {

        log.info("==>> Updating driver location | driverId={} | lat={} | lon={}",
                driverId, lat, lon);

        // Redis GEO expects (longitude, latitude)
        redisTemplate.opsForGeo().add(KEY, new Point(lon, lat), driverId);

        log.info(" ==>> Driver location saved in Redis | driverId={}", driverId);
    }


    // further use : finding nearby drivers
    public List<String> nearbyDrivers(double lat, double lon, double radiusKm, int count) {

        log.info("==>> Finding nearby drivers | lat={} | lon={} | radius={}km | limit={}",
                lat, lon, radiusKm, count);

        Circle area = new Circle(
                new Point(lon, lat),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> res =
                redisTemplate.opsForGeo().radius(KEY, area);

        if (res == null || res.getContent().isEmpty()) {
            log.warn("==>> No nearby drivers found in Redis");
            return List.of();
        }

        List<String> drivers = res.getContent().stream()
                .limit(count)
                .map(geo -> geo.getContent().getName())
                .collect(Collectors.toList());

        log.info(" ==>>Nearby drivers found: {}", drivers);

        return drivers;
    }

    public Point getDriverLocation(String driverId) {

        log.info("==>> Fetching location for driverId={}", driverId);

        List<Point> pos = redisTemplate.opsForGeo().position(KEY, driverId);

        if (pos == null || pos.isEmpty() || pos.get(0) == null) {
            log.warn("==> No location found for driverId={}", driverId);
            return null;
        }

        Point point = pos.get(0);
        log.info("===>>> Location found | driverId={} | lon={} | lat={}",
                driverId, point.getX(), point.getY());

        return point; // (lon, lat)
    }

    // for further use: removing driver location from Redis
    public void removeDriver(String driverId) {

        log.info("==>Removing driver from Redis | driverId={}", driverId);

        redisTemplate.opsForGeo().remove(KEY, driverId);

        log.info("==>Driver removed from Redis | driverId={}", driverId);
    }
}
