package com.tushargautamtgs.api_gateway.config;


import com.tushargautamtgs.api_gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {


    private final JwtUtil jwtUtil;

    private static  final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/actuator/health"
    );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)){
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return this.unauthorized(exchange,"Missing or Invalid  authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)){
            return this.unauthorized(exchange,"Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);
        List<String> roles = jwtUtil.extractRoles(token);

        var authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_"+ r))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username,null,authorities);

        // authentication into reactive context
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private boolean isPublicPath(String path){
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange,String message){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        var bufferFactory = exchange.getResponse().bufferFactory();
        var dataBuffer = bufferFactory.wrap(message.getBytes());
        return  exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

}
