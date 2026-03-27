package com.workouttrackerapi.repositories;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.models.Workouts;

public interface WorkOutRepository extends JpaRepository<Workouts, Long> {

    Workouts findByUsersIdAndScheduledDateAndScheduledTime(Long id, Date date, Time time);

    Optional<Workouts> findByIdAndUsersId(Long workoutid, Long userid);

}
