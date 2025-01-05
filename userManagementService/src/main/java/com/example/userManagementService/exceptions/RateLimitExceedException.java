package com.example.userManagementService.exceptions;

public class RateLimitExceedException extends RuntimeException{
    public RateLimitExceedException(String message){
        super(message);
    }
}
