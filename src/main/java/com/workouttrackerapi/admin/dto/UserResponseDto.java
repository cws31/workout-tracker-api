package com.workouttrackerapi.admin.dto;

import com.workouttrackerapi.auth.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean isActive;

    public UserResponseDto(Long id, String name, String email, Role role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    // getters & setters
}