package com.tushargautamtgs.ride_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null
                    && authHeader.startsWith("Bearer ")
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtUtil.validateToken(authHeader)) {

                var authentication = new UsernamePasswordAuthenticationToken(
                        jwtUtil.extractUsername(authHeader),
                        null,
                        jwtUtil.extractRoles(authHeader)
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Invalid / expired token → do NOT authenticate
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
