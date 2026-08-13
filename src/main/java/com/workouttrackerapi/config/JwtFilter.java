package com.workouttrackerapi.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.auth.service.JwtsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtsService jaJwtsService;

    @Autowired
    private UserRepositories userRepositories;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            String email = jaJwtsService.extracteEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Users user = userRepositories.findByEmail(email);

                if (user == null) {
                    logger.warn("JWT Filter Error: User not found in database for email -> " + email);
                } else if (!jaJwtsService.validateToken(email, token)) {
                    logger.warn("JWT Filter Error: Token validation failed for email -> " + email);
                } else {
                    String roleName = user.getRole() != null ? user.getRole().name() : "USER";
                    String roleWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                    String rawRole = roleName.replace("ROLE_", "");

                    // Grant both "ROLE_USER" and "USER" so hasRole("USER") and
                    // hasAuthority("ROLE_USER") both succeed
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority(roleWithPrefix),
                            new SimpleGrantedAuthority(rawRole));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("JWT Authentication failed: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}