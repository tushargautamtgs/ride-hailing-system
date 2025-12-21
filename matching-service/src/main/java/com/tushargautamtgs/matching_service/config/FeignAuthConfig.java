package com.tushargautamtgs.matching_service.config;

import com.tushargautamtgs.matching_service.security.ServiceTokenProvider;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignAuthConfig {

    private final ServiceTokenProvider tokenProvider;

    @Bean
    public RequestInterceptor serviceAuthInterceptor() {
        return template -> {
            String token = tokenProvider.getToken();

            System.out.println("[FEIGN] Attaching JWT to request");
            System.out.println("[FEIGN] Authorization = Bearer " + token);

            template.header("Authorization", "Bearer " + token);
        };
    }

}
