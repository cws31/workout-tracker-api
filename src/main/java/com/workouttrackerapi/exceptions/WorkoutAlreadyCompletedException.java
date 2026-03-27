package com.workouttrackerapi.exceptions;

public class WorkoutAlreadyCompletedException extends RuntimeException {
    public WorkoutAlreadyCompletedException(String message) {
        super(message);
    }
}