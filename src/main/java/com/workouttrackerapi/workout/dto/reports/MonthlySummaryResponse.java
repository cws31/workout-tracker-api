package com.workouttrackerapi.workout.dto.reports;

import lombok.Data;

@Data
public class MonthlySummaryResponse {

    private Long totalWorkouts;
    private Integer totalDuration;

    public MonthlySummaryResponse(Long totalWorkouts, Integer totalDuration) {
        this.totalWorkouts = totalWorkouts;
        this.totalDuration = totalDuration;
    }
}