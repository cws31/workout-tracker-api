package com.workouttrackerapi.admin.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalWorkouts;
    private long completedWorkouts;
    private long activeUsers;
    private String topExercise;
    private Map<String, Long> workoutsPerDay;

    public AdminDashboardResponse(long totalUsers, long totalWorkouts,
            long completedWorkouts, long activeUsers,
            String topExercise,
            Map<String, Long> workoutsPerDay) {

        this.totalUsers = totalUsers;
        this.totalWorkouts = totalWorkouts;
        this.completedWorkouts = completedWorkouts;
        this.activeUsers = activeUsers;
        this.topExercise = topExercise;
        this.workoutsPerDay = workoutsPerDay;
    }
}
