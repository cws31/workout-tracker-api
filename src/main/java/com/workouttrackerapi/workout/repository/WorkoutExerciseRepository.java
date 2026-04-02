package com.workouttrackerapi.workout.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.WorkoutExercises;

import java.util.*;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercises, Long> {
    @Query("""
                SELECT
                    MAX(we.weight),
                    AVG(we.reps),
                    COUNT(we.id)
                FROM WorkoutExercises we
                WHERE we.exercises.id = :exerciseId
                AND we.workouts.users.id = :userId
                AND we.workouts.staus = :status
            """)
    List<Object[]> getExerciseProgress(
            @Param("exerciseId") Long exerciseId,
            @Param("userId") Long userId,
            @Param("status") STATUS status);

    @Query("""
                SELECT e.name FROM WorkoutExercises we
                JOIN we.exercises e
                GROUP BY e.name
                ORDER BY COUNT(e.id) DESC
            """)
    List<String> findTopExercise(Pageable pageable);
}