package com.example.appointmentService.controller;


import com.example.appointmentService.models.prescription;
import com.example.appointmentService.service.prescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class prescriptionController {

    @Autowired
    private prescriptionService prescriptionService;

    @PostMapping("/doctors/{doctorId}/patients/{patientId}/prescriptions")
    public ResponseEntity<prescription> addPrescription(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody prescription prescription) {

        prescription createdPrescription = prescriptionService.addPrescription(doctorId, patientId, prescription);
        return ResponseEntity.ok(createdPrescription);
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<List<prescription>> getPrescriptionsForPatient(@PathVariable Long patientId) {
        List<prescription> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/patients/{patientId}/prescriptions/{prescriptionId}")
    public ResponseEntity<prescription> getPrescriptionById(
            @PathVariable Long patientId,
            @PathVariable Long prescriptionId) {

        prescription prescription = prescriptionService.getPrescriptionById(patientId, prescriptionId);
        return ResponseEntity.ok(prescription);
    }
}

