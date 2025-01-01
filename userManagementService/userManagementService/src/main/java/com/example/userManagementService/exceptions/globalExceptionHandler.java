package com.example.userManagementService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class globalExceptionHandler {

    @ExceptionHandler(appointmentNotFoundException.class)
    public ResponseEntity<?> handleAppointmentNotFound(appointmentNotFoundException ex) {
        return new ResponseEntity<>(new errorResponse("Appointment Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(patientNotFoundException.class)
    public ResponseEntity<?> handlePatientNotFoundException(patientNotFoundException ex) {
        return new ResponseEntity<>(new errorResponse("Patient Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(doctorNotFoundException.class)
    public ResponseEntity<?> handleDoctorNotFoundException(doctorNotFoundException ex) {
        return new ResponseEntity<>(new errorResponse("Doctor Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(userNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(userNotFoundException ex) {
        return new ResponseEntity<>(new errorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }


}
