package com.workouttrackerapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttrackerapi.models.Exercises;

public interface ExerciseRepository extends JpaRepository<Exercises, Long> {

    boolean existsByName(String name);

}
