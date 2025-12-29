package com.tushargautamtgs.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class AuthHeaderForwardFilter {

    @Bean
    public GlobalFilter authorizationHeaderFilter() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.header(HttpHeaders.AUTHORIZATION, authHeader))
                        .build();
                return chain.filter(mutatedExchange);
            }

            return chain.filter(exchange);
        };
    }
}
