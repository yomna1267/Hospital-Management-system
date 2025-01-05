package com.example.workflow_service.controller;

import com.example.workflow_service.dto.PatientStatusDTO;
import com.example.workflow_service.entities.PatientStatus;
import com.example.workflow_service.enums.Patient_Events;
import com.example.workflow_service.service.PatientStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("status")
public class PatientStatusController {

    @Autowired
    private PatientStatusService patientStatusService;


    // Endpoint to get all states for a patient
    @GetMapping("/patient/{patientId}/states")
    public ResponseEntity<List<PatientStatusDTO>> getPatientStates(@PathVariable Long patientId) {
        return patientStatusService.getStatesForOnePatient(patientId);
    }

    // Endpoint to get state for a patient and appointment
    @GetMapping("/patient/{patientId}/appointment/{appointmentId}/states")
    public ResponseEntity<PatientStatusDTO> getPatientStateWithSpecificAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {
        return patientStatusService.getStateForOnePatientWithOneAppointment(patientId, appointmentId);
    }
}
