package com.example.userManagementService.controller;

import com.example.userManagementService.dto.appointmentDTO;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.feign.appointmentClient;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.service.doctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class doctorController {
    private final doctorService doctorService;
    private final appointmentClient appointmentClient;


    public doctorController(doctorService doctorService, com.example.userManagementService.feign.appointmentClient appointmentClient) {
        this.doctorService = doctorService;
        this.appointmentClient = appointmentClient;
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

    @GetMapping("{doctorId}/appointments")
    public List<appointmentDTO> getDoctorAppointments(@PathVariable Long doctorId) {
        try{
            return appointmentClient.getAppointmentsByDoctorId(doctorId);
        }
        catch (doctorNotFoundException ex){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
    }

    @GetMapping("/doctors/{doctorId}/appointments/{appointmentId}")
    public appointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId){
        try{
            return appointmentClient.getDoctorAppointment(doctorId, appointmentId);
        }
        catch (doctorNotFoundException ex){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
    }


}
