package com.example.userManagementService.controller;

import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.service.doctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctor")
public class doctorController {
    private final doctorService doctorService;

    public doctorController(doctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<doctor> getDoctorById(@PathVariable Long id) {
        try {
            doctor doctor = doctorService.getDoctorById(id);
            return new ResponseEntity<>(doctor, HttpStatus.OK);
        } catch (patientNotFoundException ex) {
            throw new doctorNotFoundException("doctor with ID " + id + " not found");
        }
    }



}
