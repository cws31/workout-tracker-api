package com.workouttrackerapi.workout.dto.reports;

import lombok.Data;

@Data
public class ProgressResponse {

    private Double maxWeight;
    private Double avgReps;
    private Long totalSessions;

    public ProgressResponse(Double maxWeight, Double avgReps, Long totalSessions) {
        this.maxWeight = maxWeight;
        this.avgReps = avgReps;
        this.totalSessions = totalSessions;
    }
}