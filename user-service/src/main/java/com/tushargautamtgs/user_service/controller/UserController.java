package com.tushargautamtgs.user_service.controller;


import com.tushargautamtgs.user_service.dto.UpdateProfileRequest;
import com.tushargautamtgs.user_service.entity.UserProfile;
import com.tushargautamtgs.user_service.security.JwtUtil;
import com.tushargautamtgs.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Struct;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final JwtUtil jwtUtil;


    @GetMapping("/me")
    public UserProfile me(@RequestHeader("Authorization") String auth){
        String username = jwtUtil.extractUsername(auth);
        return service.getProfile(username);
    }

    @PutMapping("/me")
    public UserProfile update(
            @RequestHeader("Authorization")String auth,
            @RequestBody UpdateProfileRequest req
            ){
        String username = jwtUtil.extractUsername(auth);
        return service.update(username,req);
    }
}
