package com.workouttrackerapi.config;

import com.workouttrackerapi.auth.enums.Role;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner createAdmin(UserRepositories userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            String adminUsername = "gautamrocky909621@gmail.com";

            if (userRepository.findByEmail(adminUsername) == null) {

                Users admin = new Users();
                admin.setName("sonu kumar");
                admin.setEmail("gautamrocky909621@gmail.com");
                admin.setPassword(passwordEncoder.encode("Sonuworkout@31"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);

                userRepository.save(admin);

                System.out.println(" Default Admin Created!");
            } else {
                System.out.println(" Admin already exists");
            }
        };
    }
}