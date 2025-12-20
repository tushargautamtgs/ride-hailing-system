package com.tushargautamtgs.matching_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignAuthConfig {

    @Value("${security.service-token}")
    private String serviceToken;

    @Bean
    public RequestInterceptor serviceAuthInterceptor() {
        return requestTemplate ->
                requestTemplate.header(
                        "Authorization",
                        "Bearer " + serviceToken
                );
    }
}
