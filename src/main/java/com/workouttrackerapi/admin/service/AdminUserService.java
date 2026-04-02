package com.workouttrackerapi.admin.service;

import com.workouttrackerapi.admin.dto.UserResponseDto;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepositories userRepository;

    public AdminUserService(UserRepositories userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponseDto> listUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Users> users;
        if (search != null && !search.isEmpty()) {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search,
                    pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(u -> new UserResponseDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole(),
                u.isActive()));
    }

    public UserResponseDto getUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
    }

    public void blockUnblockUser(Long id, boolean active) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(active);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }
}