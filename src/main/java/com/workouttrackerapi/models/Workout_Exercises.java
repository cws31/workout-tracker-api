package com.workouttrackerapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Workout_Exercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sets;
    private Long reps;
    private Double weight;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workouts workouts;

    @ManyToOne
    @JoinColumn(name = "exercie_id", nullable = false)
    private Exercises exercises;
}
