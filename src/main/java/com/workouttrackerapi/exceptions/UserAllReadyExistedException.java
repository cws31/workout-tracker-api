package com.workouttrackerapi.exceptions;

public class UserAllReadyExistedException extends RuntimeException {

    public UserAllReadyExistedException(String message) {
        super(message);
    }

}
