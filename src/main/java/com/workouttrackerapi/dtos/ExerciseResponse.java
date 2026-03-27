package com.workouttrackerapi.dtos;

import com.workouttrackerapi.enums.CATEGORY;

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
