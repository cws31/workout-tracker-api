package com.workouttrackerapi.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workout_Exercises_Request {

    private Long id;
    private String name;

    @NotNull(message = "Sets is required")
    @Positive(message = "Sets must be greater than 0")
    private Long sets;

    @NotNull(message = "Reps is required")
    @Positive(message = "Reps must be greater than 0")
    private Long reps;

    @NotNull(message = "Weight is required")
    @PositiveOrZero(message = "Weight must be 0 or more")
    private Double weight;

}
