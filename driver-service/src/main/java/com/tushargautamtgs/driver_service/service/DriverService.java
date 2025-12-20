package com.tushargautamtgs.driver_service.service;


import com.tushargautamtgs.driver_service.dto.UpdateDriverRequest;
import com.tushargautamtgs.driver_service.entity.DriverProfile;
import com.tushargautamtgs.driver_service.entity.DriverStatus;
import com.tushargautamtgs.driver_service.repository.DriverProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverProfileRepository repo;
    public Optional<DriverProfile> findByUsername(String username){
        return repo.findByUsername(username);
    }


    @Transactional
    public DriverProfile updateProfile(String username, UpdateDriverRequest request){
        DriverProfile profile = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile Not Found"));

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getVehicleNumber() != null) profile.setVehichleNumber(request.getVehicleNumber());
        if (request.getVehicleType() != null) profile.setVehicleType(request.getVehicleType());

        return repo.save(profile);
    }

    @Transactional
    public DriverProfile updateStatus(String username, DriverStatus status){
        DriverProfile profile = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile Not Found"));
        profile.setStatus(status);
        return repo.save(profile);
    }

    @Transactional
    public DriverProfile createIfNotExists(String username, String name, String phone, String vehicleNumber,String vehicleType){
        return repo.findByUsername(username).orElseGet(() -> {
            DriverProfile p = DriverProfile.builder()
                    .username(username)
                    .name(name)
                    .phone(phone)
                    .vehichleNumber(vehicleNumber)
                    .vehicleType(vehicleType)
                    .active(true)
                    .status(DriverStatus.OFFLINE)
                    .build();
            return repo.save(p);
        });

    }
}
