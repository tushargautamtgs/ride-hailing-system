package com.tushargautamtgs.driver_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null) {
            try {
                // Pass the whole header (or raw token) — JwtUtil will clean "Bearer " if present
                String tokenCandidate = header;

                if (jwtUtil.validate(tokenCandidate)) {
                    String username = jwtUtil.extractUsername(tokenCandidate);

                    // don't overwrite existing authenticated principal
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        var roles = jwtUtil.extractRoles(tokenCandidate);
                        var authorities = roles.stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toList());
                        jwtUtil.extractAuthorities(tokenCandidate)
                                .forEach(a ->
                                        authorities.add(new SimpleGrantedAuthority(a))
                                );

                        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ex) {

            }
        }

        chain.doFilter(request, response);
    }
}
