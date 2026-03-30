package com.workouttrackerapi.workout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.common.exceptions.ExerciseNotFoundException;
import com.workouttrackerapi.workout.dto.WorkoutCompletionRequest;
import com.workouttrackerapi.workout.dto.WorkoutCompletionResponse;
import com.workouttrackerapi.workout.dto.WorkoutDetaiilsResponse;
import com.workouttrackerapi.workout.dto.WorkoutRequest;
import com.workouttrackerapi.workout.dto.WorkoutResponse;
import com.workouttrackerapi.workout.dto.reports.MonthlySummaryResponse;
import com.workouttrackerapi.workout.dto.reports.ProgressResponse;
import com.workouttrackerapi.workout.dto.reports.UpdateRequest;
import com.workouttrackerapi.workout.dto.reports.WorkoutHistoryResponse;
import com.workouttrackerapi.workout.enums.STATUS;

import com.workouttrackerapi.workout.service.WorkoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workout")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserRepositories userRepositories;

    public WorkoutController(WorkoutService workoutService, UserRepositories userRepositories) {
        this.workoutService = workoutService;
        this.userRepositories = userRepositories;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody WorkoutRequest workoutRequest,
            Authentication authentication) {
        Users user = userRepositories.findByEmail(authentication.getName());
        WorkoutResponse response = workoutService.createWorkOut(workoutRequest, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(@RequestBody UpdateRequest updateRequest,
            @PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepositories.findByEmail(email);
        if (user == null) {
            throw new ExerciseNotFoundException("user not Found");
        }
        return new ResponseEntity<>(workoutService.updateWorkout(id, updateRequest, user), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{workoutid}")
    public ResponseEntity<?> delete(@PathVariable Long workoutid, Authentication authentication) {
        String email = authentication.getName();
        Users users = userRepositories.findByEmail(email);
        workoutService.deleteWorkout(users, workoutid);
        return new ResponseEntity<>("delted", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get/{workoutid}")
    public ResponseEntity<?> getWorkoutDetails(Authentication authentication,
            @PathVariable Long workoutid) {
        String email = authentication.getName();
        Users user = userRepositories.findByEmail(email);
        WorkoutDetaiilsResponse response = workoutService.getWorkoutDetails(user, workoutid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<WorkoutDetaiilsResponse>> scheduledworkouts(Authentication authentication,
            @RequestParam STATUS status) {
        String email = authentication.getName();
        Users users = userRepositories.findByEmail(email);
        List<WorkoutDetaiilsResponse> responselist = workoutService.getScheduledWorkouts(users, status);
        return new ResponseEntity<>(responselist, HttpStatus.OK);

    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkoutCompletionResponse> completeWorkout(
            @PathVariable Long id,
            @RequestBody WorkoutCompletionRequest request, Authentication authrAuthentication) {
        String email = authrAuthentication.getName();
        Users user = userRepositories.findByEmail(email);
        return ResponseEntity.ok(workoutService.completeWorkout(id, user, request));
    }

    /////// ********* */ workout report controller *************//////////

    @GetMapping("/history")
    public ResponseEntity<List<WorkoutHistoryResponse>> getHistory(Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepositories.findByEmail(email);
        return ResponseEntity.ok(workoutService.getWorkoutHistory(user));
    }

    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(
            @RequestParam Long exerciseId,
            Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepositories.findByEmail(email);
        return ResponseEntity.ok(workoutService.getProgress(exerciseId, user));
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepositories.findByEmail(email);
        return ResponseEntity.ok(workoutService.getMonthlySummary(month, year, user));
    }
}
