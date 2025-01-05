package com.example.userManagementService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<?> handleAppointmentNotFound(AppointmentNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Appointment Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<?> handlePatientNotFoundException(PatientNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Patient Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<?> handleDoctorNotFoundException(DoctorNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Doctor Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500

        if (message != null && message.contains("CONFLICT")) {
            status = HttpStatus.CONFLICT;
        } else if (message != null && message.contains("FORBIDDEN")) {
            status = HttpStatus.FORBIDDEN;
        }
        else if(message != null && message.contains("BAD_REQUEST")) {
            status = HttpStatus.BAD_REQUEST;
        }
        else if(message != null && message.contains("NOT_FOUND")) {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status)
                .body(Map.of(
                        "error", "An unexpected error occurred: " + message,
                        "status", String.valueOf(status.value()),
                        "message", status.getReasonPhrase()
                ));

    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<?> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(new ErrorResponse("Access Denied", ex.getMessage(), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<?> handleInCorrectPassException(IncorrectPasswordException ex) {
        return new ResponseEntity<>(new ErrorResponse("Wrong Password", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(OtpOrTokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpired(OtpOrTokenExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RateLimitExceedException.class)
    public ResponseEntity<String> handleRateLimitExceededException(RateLimitExceedException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }


}
