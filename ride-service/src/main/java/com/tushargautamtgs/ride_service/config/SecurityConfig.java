package com.tushargautamtgs.ride_service.config;


import com.tushargautamtgs.ride_service.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
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

                        // infra
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        // 🔐 INTERNAL: matching-service assigns driver
                        .requestMatchers(
                                HttpMethod.POST,
                                "/rides/{rideId}/assign"
                        ).permitAll()

                        // 🚖 DRIVER APIs
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PATCH,
                                "/rides/*/status"
                        ).hasRole("DRIVER")

                        // 👤 USER APIs
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/rides"
                        ).authenticated()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/rides/*/validate"
                        ).permitAll()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/rides/**"
                        ).authenticated()

                        // fallback
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
