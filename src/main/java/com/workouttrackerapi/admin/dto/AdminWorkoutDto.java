package com.workouttrackerapi.admin.dto;

import java.sql.Date;
import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWorkoutDto {

    private Long id;
    private String email;
    private String title;
    private Date date;
    private Time time;
    private String status;

    public AdminWorkoutDto(Long id, String email, String title,
            Date date, Time time, String status) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.date = date;
        this.time = time;
        this.status = status;
    }

}