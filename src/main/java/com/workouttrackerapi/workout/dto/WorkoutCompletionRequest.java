package com.workouttrackerapi.workout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutCompletionRequest {

    private String notes;
    private Integer duration;

}