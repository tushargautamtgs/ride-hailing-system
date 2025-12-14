package com.tushargautamtgs.location_service.config;

import com.tushargautamtgs.location_service.security.JwtFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // public / infra
                        .requestMatchers("/actuator/health", "/error").permitAll()

                        // driver-only
                        .requestMatchers("/locations/drivers/**").hasRole("DRIVER")

                        // internal service (matching-service)
                        .requestMatchers("/locations/nearby").hasRole("DRIVER")

                        // everything else
                        .anyRequest().authenticated()
                )
                .addFilterBefore((Filter) jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
