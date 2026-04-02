package com.workouttrackerapi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.workouttrackerapi.auth.enums.Role;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.auth.service.JwtsService;

import java.util.*;
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

        String path = request.getServletPath();

        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jaJwtsService.extracteEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Users user = userRepositories.findByEmail(email);

            if (user != null && jaJwtsService.validateToken(email, token)) {

                String role = "ROLE_" + user.getRole().name();
                List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                        null, authority);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
