package com.workouttrackerapi.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.workouttrackerapi.auth.dto.UserLoginRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationResponse;
import com.workouttrackerapi.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserRegistrationResponse response = authService.regiateruser(userRegistrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        Map<String, Object> response = authService.userLogin(userLoginRequest);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
