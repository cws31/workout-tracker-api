package com.workouttrackerapi.admin.service;

import com.workouttrackerapi.admin.dto.AdminWorkoutDto;
import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.Workouts;
import com.workouttrackerapi.workout.repository.WorkOutRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class AdminWorkoutService {

    private final WorkOutRepository workoutRepository;

    public AdminWorkoutService(WorkOutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public Page<AdminWorkoutDto> getAllWorkouts(int page, int size, STATUS status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledDate").descending());

        Page<Workouts> workouts;

        if (status != null) {
            workouts = workoutRepository.findByStaus(status, pageable);
        } else {
            workouts = workoutRepository.findAll(pageable);
        }

        return workouts.map(w -> new AdminWorkoutDto(
                w.getId(),
                w.getUsers().getEmail(),
                w.getTitle(),
                w.getScheduledDate(),
                w.getScheduledTime(),
                w.getStaus().name()));
    }

    public AdminWorkoutDto getWorkout(Long id) {
        Workouts w = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        return new AdminWorkoutDto(
                w.getId(),
                w.getUsers().getEmail(),
                w.getTitle(),
                w.getScheduledDate(),
                w.getScheduledTime(),
                w.getStaus().name());
    }

    public void deleteWorkout(Long id) {
        if (!workoutRepository.existsById(id)) {
            throw new RuntimeException("Workout not found");
        }
        workoutRepository.deleteById(id); // or soft delete
    }
}