package com.tushargautamtgs.user_service.service;


import com.tushargautamtgs.user_service.dto.UpdateProfileRequest;
import com.tushargautamtgs.user_service.entity.UserProfile;
import com.tushargautamtgs.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserProfileRepository repo;

    public UserProfile getProfile(String username){
        return repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found..."));
    }
    public UserProfile update(String usernname, UpdateProfileRequest req){
        UserProfile profile = getProfile(usernname);

        profile.setFullName(req.getFullName());
        profile.setPhone(req.getPhone());
        profile.setEmail(req.getEmail());

        return repo.save(profile);
    }

    public  void createProfile(String username,String email){
        if (repo.findByUsername(username).isPresent())
            return;

        UserProfile profile = UserProfile.builder()
                .username(username)
                .email(email)
                .createdAt(java.time.Instant.now())
                .build();

        repo.save(profile);
    }
}
