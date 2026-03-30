package com.workouttrackerapi.exercise.dto;

import com.workouttrackerapi.workout.enums.CATEGORY;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponse {

    private Long exerciseId;
    private String name;
    private String description;
    private CATEGORY category;
    private String muscelGroup;

}
