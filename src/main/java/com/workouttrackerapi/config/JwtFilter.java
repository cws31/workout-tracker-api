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

        String token = authHeader.substring(7);

        try {
            String email = jaJwtsService.extracteEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Users user = userRepositories.findByEmail(email);

                if (user != null && jaJwtsService.validateToken(email, token)) {

                    String roleName = user.getRole().name();
                    String grantedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(grantedRole));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}