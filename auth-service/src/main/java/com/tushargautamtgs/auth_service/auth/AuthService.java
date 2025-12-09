package com.tushargautamtgs.auth_service.auth;


import com.tushargautamtgs.auth_service.auth.dto.AuthResponse;
import com.tushargautamtgs.auth_service.auth.dto.LoginRequest;
import com.tushargautamtgs.auth_service.auth.dto.RefreshTokenRequest;
import com.tushargautamtgs.auth_service.auth.dto.RegisterRequest;
import com.tushargautamtgs.auth_service.kafkaproducer.KafkaProducerService;
import com.tushargautamtgs.auth_service.security.JwtService;
import com.tushargautamtgs.auth_service.token.RefreshToken;
import com.tushargautamtgs.auth_service.token.RefreshTokenRepository;
import com.tushargautamtgs.auth_service.user.Role;
import com.tushargautamtgs.auth_service.user.User;
import com.tushargautamtgs.auth_service.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {



    private final KafkaProducerService procuder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw  new RuntimeException("Username already exists");
        }
        Role role = Role.valueOf(request.getRole().toUpperCase());
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .build();
        userRepository.save(user);

        //sending kafka event
        procuder.sendUserRegisteredEvent(
                user.getUsername(),
                role.name()
        );

    }


    public AuthResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String accessToken = jwtService.generateToken(user.getUsername(),roles);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken,refreshToken); // have to recheck
    }

    public AuthResponse refresh(RefreshTokenRequest request){
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));
        if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())){
            throw  new RuntimeException("Refresh token expired or revoked");
        }

        User user = token.getUser();
        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String newAccessToken = jwtService.generateToken(user.getUsername(),roles);
        String  newRefreshToken = rotateRefreshToken(token); // optional

        return new AuthResponse(newAccessToken,newRefreshToken);
    }

    private String createRefreshToken(User user){
        String tokenValue = java.util.UUID.randomUUID().toString();
        RefreshToken token = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(token);
        return tokenValue;
    }

    private String rotateRefreshToken(RefreshToken oldToken){
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        String newValue = java.util.UUID.randomUUID().toString();
        RefreshToken newToken = RefreshToken.builder()
                .token(newValue)
                .user(oldToken.getUser())
                .expiryDate(Instant.now().plus(30,ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newToken);
        return newValue;
    }
}
