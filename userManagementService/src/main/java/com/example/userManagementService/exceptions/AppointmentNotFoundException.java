package com.example.userManagementService.exceptions;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String s) {
        super(s);
    }
}
