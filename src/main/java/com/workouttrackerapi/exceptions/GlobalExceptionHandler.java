package com.workouttrackerapi.exceptions;

import com.workouttrackerapi.repositories.UserRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final UserRepositories userRepositories;

    GlobalExceptionHandler(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }

    @ExceptionHandler(UserAllReadyExistedException.class)
    public ResponseEntity<Map<String, Object>> userallreadyexisthandler(Exception exception) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "bad request");
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(loginCredentialInvalidException.class)
    public ResponseEntity<Map<String, Object>> loginExceptionHandler(Exception exception) {
        System.out.println(".....................................................................................");

        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("error", "login faild");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("Timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> exerciseNotFoundExceptionHandler(Exception exception) {
        Map<String, Object> reaposne = new HashMap<>();
        reaposne.put("message", exception.getMessage());
        reaposne.put("status", HttpStatus.NOT_FOUND);
        reaposne.put("timestamp", LocalDateTime.now());
        reaposne.put("error", "exercise are not founds");
        return new ResponseEntity<>(reaposne, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkoutSlotBookedException.class)
    public ResponseEntity<Map<String, Object>> workoutDuplicationExceptionHandler(Exception exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("error", "wrong time selection for this workout");
        response.put("Status", HttpStatus.CONFLICT);
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
