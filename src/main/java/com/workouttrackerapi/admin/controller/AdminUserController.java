package com.workouttrackerapi.admin.controller;

import com.workouttrackerapi.admin.dto.BlockUserRequestDto;
import com.workouttrackerapi.admin.dto.UserResponseDto;
import com.workouttrackerapi.admin.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponseDto> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return userService.listUsers(page, size, search);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}/block")
    public void blockUnblockUser(@PathVariable Long id, @RequestBody BlockUserRequestDto request) {
        userService.blockUnblockUser(id, request.isActive());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}