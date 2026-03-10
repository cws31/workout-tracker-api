package com.workouttrackerapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.workouttrackerapi.enums.*;
import com.workouttrackerapi.models.Exercises;
import com.workouttrackerapi.repositories.ExerciseRepository;

@Component
public class ExerciseSeeder implements CommandLineRunner {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public void ExerciseSeeders(String name, String description, CATEGORY category, String muscle_group) {
        if (!exerciseRepository.existsByName(name)) {
            Exercises exercises = new Exercises(name, description, category, muscle_group);
            exerciseRepository.save(exercises);

            System.out.println("----------------------------------------------------------------------------");
        }
    }

    @Override
    public void run(String... args) throws Exception {

        ExerciseSeeders("Push Ups", "Bodyweight chest exercise", CATEGORY.strength, "Chest");
        ExerciseSeeders("Squats", "Lower body strength exercise", CATEGORY.strength, "Legs");
        ExerciseSeeders("Bench Press", "Chest weight training exercise", CATEGORY.strength, "Chest");
        ExerciseSeeders("Deadlift", "Compound back and leg exercise", CATEGORY.strength, "Back");
        ExerciseSeeders("Running", "Cardio endurance exercise", CATEGORY.cardio, "Legs");
        ExerciseSeeders("Pull Ups", "Upper body strength exercise", CATEGORY.strength, "Back");

        ExerciseSeeders("Diamond Push Ups", "Other form of push ups ", CATEGORY.strength, "Triceps");

    }

}
