package com.workouttrackerapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 30, message = "name should be length of 4 to 30")
    private String name;
    @NotBlank(message = "email is required")
    @Email(message = "email should be in valid format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "password sholud of minimum 8 length")
    private String password;

}
