package com.workouttrackerapi.exercise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.exercise.model.Exercises;

public interface ExerciseRepository extends JpaRepository<Exercises, Long> {

    boolean existsByName(String name);

    Optional<Exercises> findByNameIgnoreCase(String name);

}
