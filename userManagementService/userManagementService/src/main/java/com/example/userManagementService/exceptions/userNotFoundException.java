package com.example.userManagementService.exceptions;

public class userNotFoundException extends RuntimeException {
    public userNotFoundException(String s) {
        super(s);
    }
}
