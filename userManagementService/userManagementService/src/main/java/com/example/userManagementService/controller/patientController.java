package com.example.userManagementService.controller;

import com.example.userManagementService.dto.appointmentDTO;
import com.example.userManagementService.dto.doctorDTO;
import com.example.userManagementService.dto.scanResultDTO;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.service.doctorService;
import com.example.userManagementService.service.patientService;
import com.example.userManagementService.feign.appointmentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient")
public class patientController {

    private final patientService patientService;
    private final appointmentClient appointmentClient;
    private final doctorService doctorService;

    @Autowired
    public patientController(patientService patientService, com.example.userManagementService.feign.appointmentClient appointmentClient, doctorService doctorService) {
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
        this.doctorService = doctorService;
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
    @GetMapping("/search/doctors")
    public ResponseEntity<List<doctorDTO>> searchDoctorsForPatient(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) Integer experienceYears,
                                                                   @RequestParam(required = false) String specialty){
        List<doctor> doctors = doctorService.searchDoctorsForPatients(name, experienceYears, specialty);
        List<doctorDTO> doctorDTOs = doctors.stream()
                .map(doctor -> new doctorDTO(
                        doctor.getUser().getFirstName(),
                        doctor.getUser().getLastName(),
                        doctor.getSpecialty(),
                        doctor.getExperienceYears()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorDTOs);
    }

    @PostMapping("/{patientId}/appointments")
    public appointmentDTO bookAppointment(@PathVariable Long patientId, @RequestBody appointmentDTO newAppointment) {
        patient patient = patientService.getPatientById(patientId);
        doctor doctor = doctorService.getDoctorById(newAppointment.getDoctorId());
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + doctor.getId() + " not found");
        }
        else{
            return appointmentClient.createAppointment(patientId, newAppointment);
        }
    }

    @PutMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO rescheduleAppointment(@PathVariable Long patientId,
                                                @PathVariable Long appointmentId,
                                                @RequestBody CharSequence newDate) {
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.updateAppointment(patientId, appointmentId, "\"" + newDate + "\"");
        }
    }


    @DeleteMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO cancelAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId) {
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.deleteAppointment(patientId, appointmentId);
        }
    }

    @GetMapping("/{patientId}/appointments")
    public List<appointmentDTO> getPatientAppointments(@PathVariable Long patientId) {
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.getAppointmentsByPatientId(patientId);
        }
    }

    @GetMapping("/{patientId}/appointments/{appointmentId}")
    public appointmentDTO getPatientAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId){
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.getPatientAppointment(patientId, appointmentId);
        }
    }
    @GetMapping("/{patientId}/scan-results/{scanId}")
    ResponseEntity<scanResultDTO> getScanResultById(@PathVariable Long patientId, @PathVariable Long scanId){
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.getScanResultById(patientId, scanId);
        }
    }
    @GetMapping("/{patientId}/scan-results")
    ResponseEntity<List<scanResultDTO>> getScanResultsForPatient(@PathVariable Long patientId){
        patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.getScanResultsForPatient(patientId);
        }
    }

}

