package com.example.userManagementService.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String s) {
        super(s);
    }
}
