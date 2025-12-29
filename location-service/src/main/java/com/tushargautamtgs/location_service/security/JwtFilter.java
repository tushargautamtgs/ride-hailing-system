package com.tushargautamtgs.location_service.security;

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
import java.util.ArrayList;
import java.util.List;

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

        System.out.println("📥 [LOCATION] Incoming Authorization = "
                + request.getHeader("Authorization"));

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(7);
            System.out.println("✅ [LOCATION] JWT received, validating...");

            if (jwtUtil.validateToken(token)) {

                String username = jwtUtil.extractUsername(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                jwtUtil.extractRoles(token)
                        .forEach(r ->
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));

                jwtUtil.extractAuthorities(token)
                        .forEach(a ->
                                authorities.add(new SimpleGrantedAuthority(a)));

                System.out.println("🔐 [LOCATION] Extracted Authorities = " + authorities);

                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("AUTH CHECK = " + auth.getAuthorities());
        } else {
            System.out.println("AUTH CHECK = ❌ NULL (unauthenticated)");
        }

        filterChain.doFilter(request, response);
    }
}
