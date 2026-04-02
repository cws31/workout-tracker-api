package com.workouttrackerapi.admin.service;

import com.workouttrackerapi.admin.dto.ExerciseRequestDto;
import com.workouttrackerapi.admin.dto.ExerciseResponseDto;
import com.workouttrackerapi.exercise.model.Exercises;
import com.workouttrackerapi.exercise.repository.ExerciseRepository;
import com.workouttrackerapi.workout.enums.CATEGORY;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminExerciseService {

    private final ExerciseRepository exerciseRepository;

    public AdminExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public ExerciseResponseDto createExercise(ExerciseRequestDto dto) {

        exerciseRepository.findByNameIgnoreCase(dto.getName())
                .ifPresent(e -> {
                    throw new RuntimeException("Exercise already exists");
                });

        Exercises exercise = new Exercises();
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setCategory(dto.getCategory());
        exercise.setMuscle_group(dto.getMuscleGroup());

        exerciseRepository.save(exercise);

        return new ExerciseResponseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getCategory(),
                exercise.getMuscle_group());
    }

    public List<ExerciseResponseDto> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(e -> new ExerciseResponseDto(
                        e.getId(),
                        e.getName(),
                        e.getCategory(),
                        e.getMuscle_group()))
                .collect(Collectors.toList());
    }

    public ExerciseResponseDto updateExercise(Long id, ExerciseRequestDto dto) {

        Exercises exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setCategory(dto.getCategory());
        exercise.setMuscle_group(dto.getMuscleGroup());

        exerciseRepository.save(exercise);

        return new ExerciseResponseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getCategory(),
                exercise.getMuscle_group());
    }

    public void deleteExercise(Long id) {

        if (!exerciseRepository.existsById(id)) {
            throw new RuntimeException("Exercise not found");
        }

        exerciseRepository.deleteById(id);
    }
}