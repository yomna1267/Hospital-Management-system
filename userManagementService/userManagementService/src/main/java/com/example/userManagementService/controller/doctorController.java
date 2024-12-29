package com.example.userManagementService.controller;

import com.example.userManagementService.dto.appointmentDTO;
import com.example.userManagementService.dto.patientDTO;
import com.example.userManagementService.dto.scanResultDTO;
import com.example.userManagementService.dto.workingHoursDTO;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.feign.appointmentClient;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.repository.patientRepository;
import com.example.userManagementService.service.doctorService;
import com.example.userManagementService.service.patientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class doctorController {
    private final doctorService doctorService;
    private final patientService patientService;
    private final appointmentClient appointmentClient;
    private final com.example.userManagementService.repository.patientRepository patientRepository;


    public doctorController(doctorService doctorService, patientService patientService, appointmentClient appointmentClient, patientRepository patientRepository) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
        this.patientRepository = patientRepository;
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

    @GetMapping("/{doctorId}/appointments")
    public List<appointmentDTO> getDoctorAppointments(@PathVariable Long doctorId) {
        doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else {
             return appointmentClient.getAppointmentsByDoctorId(doctorId);
        }
    }

    @GetMapping("/doctors/{doctorId}/appointments/{appointmentId}")
    public appointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId){
        doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else {
            return appointmentClient.getDoctorAppointment(doctorId, appointmentId);
        }
    }

    @GetMapping("/{doctorId}/search/{patientId}")
    public patientDTO searchPatientById(@PathVariable Long doctorId, @PathVariable Long patientId) {
        List<appointmentDTO> doctorAppointments = getDoctorAppointments(doctorId);
        appointmentDTO patientAppointment = doctorAppointments
                .stream()
                .filter(appointment -> appointment.getPatientId().equals(patientId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Appointment not found for patient with ID: " + patientId));
        patientDTO patient = patientService.mapToPatientDTO(patientAppointment);
        return patient;
    }

    @PostMapping("/{doctorId}/patient/{patientId}/scan-results")
    public ResponseEntity<scanResultDTO> addScanResult(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody scanResultDTO scanResultDTO){
        doctor doctor = doctorService.getDoctorById(doctorId);
        patient patient = patientService.getPatientById(patientId);
        if (doctor == null){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else if(patient == null){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else{
            return appointmentClient.addScanResult(doctorId, patientId, scanResultDTO);
        }

    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<workingHoursDTO>> getAvailability(@PathVariable Long id){
        doctor doctor = doctorService.getDoctorById(id);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + id + " not found");
        }
        return doctorService.getAvailability(id);
    }

}
