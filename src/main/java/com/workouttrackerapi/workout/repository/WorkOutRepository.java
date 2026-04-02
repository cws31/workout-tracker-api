package com.workouttrackerapi.workout.repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.Workouts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOutRepository extends JpaRepository<Workouts, Long> {

    Workouts findByUsersIdAndScheduledDateAndScheduledTime(Long id, Date date, Time time);

    Optional<Workouts> findByIdAndUsersId(Long workoutid, Long userid);

    List<Workouts> findByUsersIdAndStaus(Long userid, STATUS status, Sort sort);

    long countByStaus(STATUS status);

    Page<Workouts> findByStaus(STATUS status, Pageable pageable);

    Page<Workouts> findByUsersId(Long userId, Pageable pageable);

    Page<Workouts> findByScheduledDate(Date date, Pageable pageable);

    @Query("""
                SELECT w.scheduledDate, COUNT(w)
                FROM Workouts w
                WHERE w.staus = com.workouttrackerapi.workout.enums.STATUS.COMPLETED
                GROUP BY w.scheduledDate
                ORDER BY w.scheduledDate DESC
            """)
    List<Object[]> countCompletedWorkoutsPerDay();

}
