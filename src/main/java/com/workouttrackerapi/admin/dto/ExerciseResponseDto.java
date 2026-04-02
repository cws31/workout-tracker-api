package com.workouttrackerapi.admin.dto;

import com.workouttrackerapi.workout.enums.CATEGORY;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseResponseDto {

    private Long id;
    private String name;
    private CATEGORY category;
    private String muscleGroup;

    public ExerciseResponseDto(Long id, String name, CATEGORY category, String muscleGroup) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.muscleGroup = muscleGroup;
    }

}