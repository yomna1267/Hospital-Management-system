package com.example.userManagementService.exceptions;

public class IncorrectPasswordException extends RuntimeException{
    public IncorrectPasswordException(String message){
         super(message);
    }
}
