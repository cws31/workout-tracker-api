package com.workouttrackerapi.exercise.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.workouttrackerapi.common.exceptions.*;
import com.workouttrackerapi.exercise.dto.ExerciseResponse;
import com.workouttrackerapi.exercise.model.Exercises;
import com.workouttrackerapi.exercise.repository.ExerciseRepository;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;

    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> returnAllExercises() {

        List<Exercises> existedExe = exerciseRepository.findAll();
        if (existedExe.isEmpty()) {
            throw new ExerciseNotFoundException(" exercises are not available");
        }
        ArrayList<ExerciseResponse> list = new ArrayList<>();
        for (Exercises exe : existedExe) {
            ExerciseResponse exerciseResponse = new ExerciseResponse();
            exerciseResponse.setExerciseId(exe.getId());
            exerciseResponse.setName(exe.getName());
            exerciseResponse.setDescription(exe.getDescription());
            exerciseResponse.setCategory(exe.getCategory());
            exerciseResponse.setMuscelGroup(exe.getMuscle_group());
            list.add(exerciseResponse);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
