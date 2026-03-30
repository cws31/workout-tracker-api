package com.workouttrackerapi.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.exercise.model.Exercises;

public interface ExerciseRepository extends JpaRepository<Exercises, Long> {

    boolean existsByName(String name);

}
