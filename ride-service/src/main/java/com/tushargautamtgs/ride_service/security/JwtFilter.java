//package com.tushargautamtgs.ride_service.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        try {
//            if (authHeader != null
//                    && authHeader.startsWith("Bearer ")
//                    && SecurityContextHolder.getContext().getAuthentication() == null
//                    && jwtUtil.validateToken(authHeader)) {
//
//                String username = jwtUtil.extractUsername(authHeader);
//                List<String> roles = jwtUtil.extractRoles(authHeader);
//
//                var authorities = roles.stream()
//                        .map(role -> {
//                            // 🔐 service-to-service
//                            if (role.startsWith("SERVICE_")) {
//                                return new SimpleGrantedAuthority(role);
//                            }
//                            // 👤 user / driver
//                            return new SimpleGrantedAuthority("ROLE_" + role);
//                        })
//                        .toList();
//
//                var authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                username,
//                                null,
//                                authorities
//                        );
//
//                SecurityContextHolder.getContext()
//                        .setAuthentication(authentication);
//            }
//        } catch (Exception ex) {
//            // invalid / expired token → clear context
//            SecurityContextHolder.clearContext();
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//
//}

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

        String header = request.getHeader("Authorization");

        if (header != null
                && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.validateToken(header)) {

            String username = jwtUtil.extractUsername(header);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            jwtUtil.extractRoles(header).forEach(
                    r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r))
            );

            jwtUtil.extractAuthorities(header).forEach(
                    a -> authorities.add(new SimpleGrantedAuthority(a))
            );

            var auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
