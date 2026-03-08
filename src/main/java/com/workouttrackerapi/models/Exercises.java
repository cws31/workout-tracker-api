package com.workouttrackerapi.models;

import com.workouttrackerapi.enums.CATEGORY;
import java.util.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Exercises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private CATEGORY category;
    private String muscle_group;

    @OneToMany(mappedBy = "exercises", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Workout_Exercises> workout_Exercises;

}
