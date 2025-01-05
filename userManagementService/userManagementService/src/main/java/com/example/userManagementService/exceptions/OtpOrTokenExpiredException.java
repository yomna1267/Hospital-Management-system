package com.example.userManagementService.exceptions;

public class OtpOrTokenExpiredException extends RuntimeException {
    public OtpOrTokenExpiredException(String message) {
        super(message);
    }
}
