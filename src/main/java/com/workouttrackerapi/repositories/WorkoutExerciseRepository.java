package com.workouttrackerapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
import com.workouttrackerapi.models.Workout_Exercises;

public interface WorkoutExerciseRepository extends JpaRepository<Workout_Exercises, Long> {

    @Query("""
                SELECT
                    MAX(we.weight),
                    AVG(we.reps),
                    COUNT(we.id)
                FROM Workout_Exercises we
                WHERE we.exercises.id = :exerciseId
                AND we.workouts.users.id = :userId
                AND we.workouts.staus = com.workouttrackerapi.enums.STATUS.COMPLETED
            """)
    List<Object[]> getExerciseProgress(Long exerciseId, Long userId);
}