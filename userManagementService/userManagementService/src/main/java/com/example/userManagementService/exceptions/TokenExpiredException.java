package com.example.userManagementService.exceptions;

public class TokenExpiredException extends RuntimeException{
    public  TokenExpiredException(String message){
        super(message);
    }
}
