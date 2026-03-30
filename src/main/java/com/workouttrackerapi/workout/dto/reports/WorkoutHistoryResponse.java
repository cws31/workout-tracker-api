package com.workouttrackerapi.workout.dto.reports;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class WorkoutHistoryResponse {

    private String workoutName;
    private LocalDate completionDate;
    private Integer duration;
    private String notes;

    public WorkoutHistoryResponse(String workoutName, LocalDate completionDate, Integer duration, String notes) {
        this.workoutName = workoutName;
        this.completionDate = completionDate;
        this.duration = duration;
        this.notes = notes;
    }
}
