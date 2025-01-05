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

}
