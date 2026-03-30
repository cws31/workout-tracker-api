package com.workouttrackerapi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.auth.model.Users;

public interface UserRepositories extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

}
