package com.workouttrackerapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workouttrackerapi.dtos.UserRegistrationRequest;
import com.workouttrackerapi.dtos.UserRegistrationResponse;
import com.workouttrackerapi.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(
            @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserRegistrationResponse response = authService.regiateruser(userRegistrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
