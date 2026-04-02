package com.workouttrackerapi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.auth.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositories extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

    long countByIsActiveTrue();

    Page<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email,
            Pageable pageable);

}
