package com.workouttrackerapi.workout.dto;

import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

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

    @Valid
    @Size(min = 1, message = "workout should have at least one exercise")
    private List<Workout_Exercises_Request> workout_Exercises_Request;

}
