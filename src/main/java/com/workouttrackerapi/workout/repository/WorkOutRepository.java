package com.workouttrackerapi.workout.repository;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.Workouts;

public interface WorkOutRepository extends JpaRepository<Workouts, Long> {

    Workouts findByUsersIdAndScheduledDateAndScheduledTime(Long id, Date date, Time time);

    Optional<Workouts> findByIdAndUsersId(Long workoutid, Long userid);

    List<Workouts> findByUsersIdAndStaus(Long userid, STATUS status, Sort sort);

}
