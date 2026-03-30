package com.workouttrackerapi.auth.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

import com.workouttrackerapi.auth.dto.UserLoginRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationResponse;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.common.exceptions.UserAllReadyExistedException;
import com.workouttrackerapi.common.exceptions.loginCredentialInvalidException;

@Service
public class AuthService {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    private final JwtsService jwtsService;

    public AuthService(JwtsService jwtsService, UserRepositories userRepositories, PasswordEncoder passwordEncoder) {
        this.jwtsService = jwtsService;
        this.userRepositories = userRepositories;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegistrationResponse regiateruser(UserRegistrationRequest userRegistrationRequest) {
        Users user = new Users();

        Users ExistedUser = userRepositories.findByEmail(userRegistrationRequest.getEmail());
        if (ExistedUser != null) {
            throw new UserAllReadyExistedException("user all ready existed with this email");
        }

        user.setName(userRegistrationRequest.getName());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole("ROLE_USER");
        Users saveduser = userRepositories.save(user);
        return new UserRegistrationResponse(saveduser.getId(), saveduser.getName(), saveduser.getEmail());

    }

    public Map<String, Object> userLogin(UserLoginRequest userLoginRequest) {
        Users user = userRepositories.findByEmail(userLoginRequest.getEmail());

        if (user == null) {
            throw new loginCredentialInvalidException(
                    "invalid username or password");
        }
        String password = user.getPassword();
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), password)) {
            throw new loginCredentialInvalidException(
                    "invalid username or password");
        }

        Map<String, Object> response = new HashMap<>();
        String token = jwtsService.generateToken(userLoginRequest.getEmail(), user.getRole());
        response.put("token", token);
        return response;
    }

}
