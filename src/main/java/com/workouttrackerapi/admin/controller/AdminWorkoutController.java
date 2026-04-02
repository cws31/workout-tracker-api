package com.workouttrackerapi.admin.controller;

import com.workouttrackerapi.admin.dto.AdminWorkoutDto;
import com.workouttrackerapi.admin.service.AdminWorkoutService;
import com.workouttrackerapi.workout.enums.STATUS;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/workouts")
public class AdminWorkoutController {

    private final AdminWorkoutService service;

    public AdminWorkoutController(AdminWorkoutService service) {
        this.service = service;
    }

    @GetMapping
    public Page<AdminWorkoutDto> getAllWorkouts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) STATUS status) {
        return service.getAllWorkouts(page, size, status);
    }

    @GetMapping("/{id}")
    public AdminWorkoutDto getWorkout(@PathVariable Long id) {
        return service.getWorkout(id);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkout(@PathVariable Long id) {
        service.deleteWorkout(id);
    }
}