package com.workouttrackerapi.admin.dto;

import com.workouttrackerapi.workout.enums.CATEGORY;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRequestDto {

    @NotBlank(message = "exercise name must not be empty it should have at leas of 5 size")
    @Size(min = 5, message = "exercise name should at least of 5 ")
    private String name;

    @NotBlank(message = "must have to add description of the exercise")
    private String description;

    @NotNull(message = "must have to speicify the category while adding or updating the exercise")
    private CATEGORY category;

    @NotBlank(message = "must have to speicify the muscle_group while adding or updating the exercise")
    private String muscleGroup;

}