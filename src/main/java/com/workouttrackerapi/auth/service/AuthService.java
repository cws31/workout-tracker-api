package com.workouttrackerapi.auth.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

import com.workouttrackerapi.auth.dto.UserLoginRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationResponse;
import com.workouttrackerapi.auth.enums.Role;
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

        Users existedUser = userRepositories.findByEmail(userRegistrationRequest.getEmail());
        if (existedUser != null) {
            throw new UserAllReadyExistedException("User already exists with this email");
        }

        Users user = new Users();
        user.setName(userRegistrationRequest.getName());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));

        user.setRole(Role.USER);

        user.setActive(true);

        Users savedUser = userRepositories.save(user);

        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail());
    }

    public Map<String, Object> userLogin(UserLoginRequest userLoginRequest) {

        Users user = userRepositories.findByEmail(userLoginRequest.getEmail());

        if (user == null) {
            throw new loginCredentialInvalidException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new loginCredentialInvalidException("Account is blocked by admin");
        }

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new loginCredentialInvalidException("Invalid email or password");
        }

        String token = jwtsService.generateToken(
                user.getEmail(),
                user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("userId", user.getId());

        return response;
    }

}
