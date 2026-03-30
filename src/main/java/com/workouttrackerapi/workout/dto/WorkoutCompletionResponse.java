package com.workouttrackerapi.workout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class WorkoutCompletionResponse {

    private String message;
    private String status;

    public WorkoutCompletionResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}