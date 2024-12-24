package com.example.userManagementService.controller;

import com.example.userManagementService.dto.appointmentDTO;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.service.patientService;
import com.example.userManagementService.feign.appointmentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class patientController {

    @Autowired
    private final patientService patientService;
    private final appointmentDTO appointmentDTO;

    @Autowired
    private appointmentClient appointmentClient;

    @Autowired
    public patientController(patientService patientService, appointmentDTO appointmentDTO) {
        this.patientService = patientService;
        this.appointmentDTO = appointmentDTO;
    }



    @GetMapping("/{id}")
    public ResponseEntity<patient> getPatientById(@PathVariable Long id) {
        try {
            patient patient = patientService.getPatientById(id);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        } catch (patientNotFoundException ex) {
            throw new patientNotFoundException("Patient with ID " + id + " not found");
        }
    }

    @PostMapping("/{patientId}/appointments")
    public appointmentDTO bookAppointment(@PathVariable Long patientId, @RequestBody appointmentDTO newAppointment) {
        return appointmentClient.createAppointment(patientId, newAppointment);
    }

    @PutMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO rescheduleAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId, @RequestBody CharSequence newDate) {
        return appointmentClient.updateAppointment(patientId, appointmentId, "\"" + newDate + "\"");
    }

    @DeleteMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO cancelAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId) {
        return appointmentClient.deleteAppointment(patientId, appointmentId);
    }

    @GetMapping("{patientId}/appointments")
    public List<appointmentDTO> getPatientAppointments(@PathVariable Long patientId) {
        try{        return appointmentClient.getAppointmentsByPatientId(patientId);
        }
        catch (patientNotFoundException ex){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
         }
        }
    }