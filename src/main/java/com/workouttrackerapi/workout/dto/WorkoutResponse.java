package com.workouttrackerapi.workout.dto;

import java.util.*;

import com.workouttrackerapi.auth.model.Users;

import com.workouttrackerapi.workout.model.WorkoutExercises;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutResponse {

    private String title;
    private String description;
    private Date scheduledDate;
    private Time scheduledTime;
    private Long userId;
    private List<Workout_Exercises_Request> workout_Exercises_Request;

}
