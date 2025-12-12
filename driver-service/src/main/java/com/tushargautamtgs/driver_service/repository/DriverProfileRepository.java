package com.tushargautamtgs.driver_service.repository;

import com.tushargautamtgs.driver_service.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DriverProfileRepository extends JpaRepository<DriverProfile,Long> {
    Optional<DriverProfile> findByUsername(String username);

}
