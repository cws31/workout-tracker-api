package com.workouttrackerapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.models.Users;

public interface UserRepositories extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

}
