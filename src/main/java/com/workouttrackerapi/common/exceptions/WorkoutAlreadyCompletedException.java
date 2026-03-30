package com.workouttrackerapi.common.exceptions;

public class WorkoutAlreadyCompletedException extends RuntimeException {
    public WorkoutAlreadyCompletedException(String message) {
        super(message);
    }
}