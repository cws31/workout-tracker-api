package com.workouttrackerapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;
import com.workouttrackerapi.models.WorkoutHistory;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long> {
    List<WorkoutHistory> findByWorkout_Users_Id(Long userId);

    @Query("""
                SELECT
                    COUNT(h),
                    SUM(h.duration)
                FROM WorkoutHistory h
                WHERE h.workout.users.id = :userId
                AND MONTH(h.completionDate) = :month
                AND YEAR(h.completionDate) = :year
            """)
    List<Object[]> getMonthlySummary(Long userId, int month, int year);

}