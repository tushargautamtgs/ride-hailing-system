package com.tushargautamtgs.driver_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    private String phone;
    private String email;

    @Column(unique = true)
    private String vehichleNumber;

    private String vehicleType;

    private boolean active =  true;

    private Instant createdAt;

    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;



    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public  void preUpdate() {
        updatedAt = Instant.now();
    }
}
