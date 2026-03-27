package com.workouttrackerapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workout_Exercises_Request {

    private Long id;
    private String name;
    private Long sets;
    private Long reps;
    private Double weight;

}
