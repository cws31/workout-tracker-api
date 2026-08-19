package com.workouttrackerapi.workout.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workouttrackerapi.exercise.model.Exercises;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WorkoutExercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    @JsonIgnore
    private Workouts workouts;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercises exercises;

    // Replaced flat sets, reps, and weight with individual sets mapping
    @OneToMany(mappedBy = "workoutExercises", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WorkoutSets> workoutSets;
}