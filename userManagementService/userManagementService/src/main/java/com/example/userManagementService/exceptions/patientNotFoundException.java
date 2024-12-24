package com.example.userManagementService.exceptions;

public class patientNotFoundException extends RuntimeException {
    public patientNotFoundException(String message) {
        super(message);
    }
}
