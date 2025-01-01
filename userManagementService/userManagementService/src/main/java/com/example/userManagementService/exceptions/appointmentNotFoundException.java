package com.example.userManagementService.exceptions;

public class appointmentNotFoundException extends RuntimeException {
    public appointmentNotFoundException(String s) {
        super(s);
    }
}
