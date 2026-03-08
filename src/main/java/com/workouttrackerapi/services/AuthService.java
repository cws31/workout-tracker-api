package com.workouttrackerapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityProperties.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workouttrackerapi.dtos.UserRegistrationRequest;
import com.workouttrackerapi.dtos.UserRegistrationResponse;
import com.workouttrackerapi.exceptions.UserAllReadyExistedException;
import com.workouttrackerapi.models.Users;
import com.workouttrackerapi.repositories.UserRepositories;

@Service
public class AuthService {

    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

}
