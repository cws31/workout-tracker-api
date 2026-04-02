package com.workouttrackerapi.admin.controller;

import com.workouttrackerapi.admin.dto.ExerciseRequestDto;
import com.workouttrackerapi.admin.dto.ExerciseResponseDto;
import com.workouttrackerapi.admin.service.AdminExerciseService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/exercises")
public class AdminExerciseController {

    private final AdminExerciseService service;

    public AdminExerciseController(AdminExerciseService service) {
        this.service = service;
    }

    @PostMapping
    public ExerciseResponseDto createExercise(@RequestBody ExerciseRequestDto dto) {
        return service.createExercise(dto);
    }

    @GetMapping
    public List<ExerciseResponseDto> getAllExercises() {
        return service.getAllExercises();
    }

    @PutMapping("/{id}")
    public ExerciseResponseDto updateExercise(@PathVariable Long id,
            @RequestBody ExerciseRequestDto dto) {
        return service.updateExercise(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteExercise(@PathVariable Long id) {
        service.deleteExercise(id);
    }
}