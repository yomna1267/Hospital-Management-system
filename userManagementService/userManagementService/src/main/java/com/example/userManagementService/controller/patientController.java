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

    private final patientService patientService;
    private final appointmentClient appointmentClient;

    @Autowired
    public patientController(patientService patientService, com.example.userManagementService.feign.appointmentClient appointmentClient) {
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
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
    public appointmentDTO rescheduleAppointment(@PathVariable Long patientId,
                                                @PathVariable Long appointmentId,
                                                @RequestBody CharSequence newDate) {
        return appointmentClient.updateAppointment(patientId, appointmentId, "\"" + newDate + "\"");
    }


    @DeleteMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO cancelAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId) {
        return appointmentClient.deleteAppointment(patientId, appointmentId);
    }

    @GetMapping("{patientId}/appointments")
    public List<appointmentDTO> getPatientAppointments(@PathVariable Long patientId) {
        try{
            return appointmentClient.getAppointmentsByPatientId(patientId);
        }
        catch (patientNotFoundException ex){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
    }

    @GetMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO getPatientAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId){
        try{
            return appointmentClient.getPatientAppointment(patientId, appointmentId);
        }
        catch (patientNotFoundException ex){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
    }


}

