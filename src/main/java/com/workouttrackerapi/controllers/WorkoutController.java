package com.workouttrackerapi.controllers;

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
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.workouttrackerapi.dtos.UpdateRequest;
import com.workouttrackerapi.dtos.WorkoutDetaiilsResponse;
import com.workouttrackerapi.dtos.WorkoutRequest;
import com.workouttrackerapi.dtos.WorkoutResponse;
import com.workouttrackerapi.exceptions.ExerciseNotFoundException;
import com.workouttrackerapi.models.Users;
import com.workouttrackerapi.models.Workouts;
import com.workouttrackerapi.repositories.UserRepositories;
import com.workouttrackerapi.services.WorkoutService;

@RestController
@RequestMapping("api/workout")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserRepositories userRepositories;

    public WorkoutController(WorkoutService workoutService, UserRepositories userRepositories) {
        this.workoutService = workoutService;
        this.userRepositories = userRepositories;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@RequestBody WorkoutRequest workoutRequest,
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

}
