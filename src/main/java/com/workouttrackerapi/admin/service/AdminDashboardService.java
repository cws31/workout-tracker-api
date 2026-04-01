package com.workouttrackerapi.admin.service;

import com.workouttrackerapi.admin.dto.AdminDashboardResponse;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.repository.WorkOutRepository;
import com.workouttrackerapi.workout.repository.WorkoutExerciseRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepositories userRepository;
    private final WorkOutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public AdminDashboardService(UserRepositories userRepository,
            WorkOutRepository workoutRepository,
            WorkoutExerciseRepository workoutExerciseRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public AdminDashboardResponse getDashboardData() {

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();

        long totalWorkouts = workoutRepository.count();
        long completedWorkouts = workoutRepository.countByStaus(STATUS.COMPLETED);

        String topExercise = workoutExerciseRepository
                .findTopExercise(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse("N/A");

        return new AdminDashboardResponse(
                totalUsers,
                totalWorkouts,
                completedWorkouts,
                activeUsers,
                topExercise);
    }
}