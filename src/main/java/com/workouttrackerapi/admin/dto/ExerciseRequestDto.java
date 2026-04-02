package com.workouttrackerapi.admin.dto;

import com.workouttrackerapi.workout.enums.CATEGORY;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRequestDto {

    private String name;
    private String description;
    private CATEGORY category;
    private String muscleGroup;

}