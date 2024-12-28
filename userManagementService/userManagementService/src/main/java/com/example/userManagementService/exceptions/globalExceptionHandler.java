package com.example.userManagementService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class globalExceptionHandler {

    @ExceptionHandler(patientNotFoundException.class)
    public ResponseEntity<?> handlePatientNotFoundException(patientNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(doctorNotFoundException.class)
    public ResponseEntity<?> handleDoctorNotFoundException(doctorNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(userNotFoundException.class)
    public ResponseEntity<?> handleAdminNotFoundException(userNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }

}
