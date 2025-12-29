package com.tushargautamtgs.user_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Instant createdAt;

    // if not req then I'll remove it
    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
