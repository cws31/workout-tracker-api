package com.workouttrackerapi.workout.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workout_Exercises_Request {

    private Long id;
    private String name;

    @Valid
    @NotEmpty(message = "Workout exercise should have at least one set")
    private List<WorkoutSetRequest> sets;
}