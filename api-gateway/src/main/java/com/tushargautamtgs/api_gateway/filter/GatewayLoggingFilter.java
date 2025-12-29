package com.tushargautamtgs.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.cloud.gateway.route.Route;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
@Slf4j
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {

                    long timeTaken = System.currentTimeMillis() - startTime;

                    URI routedUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

                    Route route = exchange.getAttribute("org.springframework.cloud.gateway.route.Route");
                    String routeId = route != null ? route.getId() : "N/A";

                    Integer statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    log.info(
                            "🌐 GATEWAY | {} {} | route={} | instance={} | status={} | time={}ms",
                            method,
                            path,
                            routeId,
                            routedUri,
                            statusCode,
                            timeTaken
                    );
                })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
