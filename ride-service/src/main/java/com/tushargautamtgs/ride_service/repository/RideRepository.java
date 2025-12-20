package com.tushargautamtgs.ride_service.repository;

import com.tushargautamtgs.ride_service.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {
}
