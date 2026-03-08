package com.workouttrackerapi.models;

import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;

import org.hibernate.annotations.CreationTimestamp;
import java.util.*;
import com.workouttrackerapi.enums.STATUS;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Workouts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Date scheduled_date;
    private Time scheduled_time;
    private STATUS staus;
    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @OneToMany(mappedBy = "workouts")
    private List<Workout_Exercises> workout_Exercises;

    @OneToMany(mappedBy = "workouts", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Workout_Logs> workout_Logs;

}
