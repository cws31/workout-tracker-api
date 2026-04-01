package com.workouttrackerapi.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalWorkouts;
    private long completedWorkouts;
    private long activeUsers;
    private String topExercise;

    public AdminDashboardResponse(long totalUsers, long totalWorkouts,
            long completedWorkouts, long activeUsers,
            String topExercise) {
        this.totalUsers = totalUsers;
        this.totalWorkouts = totalWorkouts;
        this.completedWorkouts = completedWorkouts;
        this.activeUsers = activeUsers;
        this.topExercise = topExercise;
    }

}