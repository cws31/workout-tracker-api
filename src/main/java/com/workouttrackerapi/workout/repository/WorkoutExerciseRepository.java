package com.workouttrackerapi.workout.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.WorkoutExercises;
import com.workouttrackerapi.workout.model.WorkoutSets;

import java.util.*;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercises, Long> {

    @Query("""
                SELECT
                    MAX(ws.weight),
                    AVG(ws.reps),
                    COUNT(ws.id)
                FROM WorkoutExercises we
                JOIN we.workoutSets ws
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

    @Query("""
                SELECT ws
                FROM WorkoutExercises we
                JOIN we.workoutSets ws
                JOIN we.workouts w
                WHERE we.exercises.id = :exerciseId
                AND w.users.id = :userId
                AND w.staus = :status
                ORDER BY w.scheduledDate DESC, w.scheduledTime DESC, ws.setNumber ASC
            """)
    List<WorkoutSets> findLastCompletedExerciseSets(
            @Param("exerciseId") Long exerciseId,
            @Param("userId") Long userId,
            @Param("status") STATUS status,
            Pageable pageable);
}