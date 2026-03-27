package com.workouttrackerapi.dtos;

import java.util.*;

import com.workouttrackerapi.models.Workout_Exercises;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutDetaiilsResponse {

    private String title;
    private String description;
    private Date scheduledDate;
    private Time scheduledTime;
    private List<Workout_Exercises_Request> exercises;

}
