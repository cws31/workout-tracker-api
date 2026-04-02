package com.workouttrackerapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.workouttrackerapi.common.exceptions.AccessDeniedExceptionHandler;
import com.workouttrackerapi.common.exceptions.EntryPointExceptionHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 🔥 IMPORTANT (for @PreAuthorize)
public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Autowired
        private EntryPointExceptionHandler entryPointExceptionHandler;

        @Autowired
        private AccessDeniedExceptionHandler accessDeniedExceptionHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                .requestMatchers("/api/workouts/**").hasAnyRole("USER", "ADMIN")

                                                .anyRequest().authenticated())

                                .exceptionHandling(exp -> exp
                                                .authenticationEntryPoint(entryPointExceptionHandler)
                                                .accessDeniedHandler(accessDeniedExceptionHandler))

                                .httpBasic(httpbasic -> httpbasic.disable())

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}