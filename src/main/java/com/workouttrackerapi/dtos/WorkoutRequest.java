package com.workouttrackerapi.dtos;

import java.util.*;
import java.sql.Date;
import java.sql.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutRequest {

    private String title;
    private String description;
    private Date scheduled_date;
    private Time scheduled_time;

    private List<Workout_Exercises_Request> workout_Exercises_Request;

}
