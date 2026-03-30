package com.workouttrackerapi.workout.dto.reports;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

import com.workouttrackerapi.workout.dto.Workout_Exercises_Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

    private Date changeScheduedDate;

    private Time changeScheduedTime;

    private String comments;

    private List<Workout_Exercises_Request> addExecise;

}
