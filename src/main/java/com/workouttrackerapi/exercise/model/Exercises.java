package com.workouttrackerapi.exercise.model;

import java.util.*;

import com.workouttrackerapi.workout.enums.CATEGORY;
import com.workouttrackerapi.workout.model.WorkoutExercises;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Exercises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private CATEGORY category;
    private String muscle_group;

    public Exercises(String name, String description, CATEGORY category, String muscle_group) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.muscle_group = muscle_group;
    }

    @OneToMany(mappedBy = "exercises", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExercises> workout_Exercises;

}
