package com.workouttrackerapi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.workouttrackerapi.models.Users;
import com.workouttrackerapi.repositories.UserRepositories;
import com.workouttrackerapi.services.JwtsService;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jaJwtsService.extracteEmail(token);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Users user = userRepositories.findByEmail(email);
            String role = user.getRole();
            if (user != null && jaJwtsService.validateToken(email, token)) {
                List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority(role));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                        null, authority);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}
