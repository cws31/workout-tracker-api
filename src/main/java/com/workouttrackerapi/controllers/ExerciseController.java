package com.workouttrackerapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import com.workouttrackerapi.models.Exercises;
import com.workouttrackerapi.repositories.ExerciseRepository;
import com.workouttrackerapi.exceptions.*;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;

    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Exercises>> returnAllExercises() {
        List<Exercises> response = exerciseRepository.findAll();
        if (response.isEmpty()) {
            throw new ExerciseNotFoundException("no exercises are available");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
