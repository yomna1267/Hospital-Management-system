package com.example.workflow_service.controller;

import com.example.workflow_service.dto.PatientStatusDTO;
import com.example.workflow_service.entities.PatientStatus;
import com.example.workflow_service.enums.Patient_Events;
import com.example.workflow_service.service.PatientStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("status")
public class PatientStatusController {

    @Autowired
    private PatientStatusService patientStatusService;

    @PostMapping("/{patientId}/{appointmentId}")
    public ResponseEntity<PatientStatusDTO> createState(@PathVariable Long patientId, @PathVariable Long appointmentId) {
        PatientStatusDTO patientStatusDTO = patientStatusService.createRegisteredEvent(patientId, appointmentId);
        System.out.println(patientStatusDTO);
        return new ResponseEntity<PatientStatusDTO>(patientStatusDTO,HttpStatus.CREATED);
    }

    @PostMapping("/{patientId}/{appointmentId}/{event}")
    public ResponseEntity<PatientStatusDTO> processEvent(@PathVariable Long patientId, @PathVariable Long appointmentId, @PathVariable Patient_Events event) {
        PatientStatusDTO patientStatusDTO = patientStatusService.handleEvents(patientId, appointmentId, event);
        return new ResponseEntity<PatientStatusDTO>(patientStatusDTO,HttpStatus.OK);
    }
}
