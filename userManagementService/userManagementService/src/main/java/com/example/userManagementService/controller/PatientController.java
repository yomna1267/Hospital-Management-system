package com.example.userManagementService.controller;

import com.example.userManagementService.dto.AppointmentDTO;
import com.example.userManagementService.dto.DoctorDTO;
import com.example.userManagementService.dto.ScanResultDTO;
import com.example.userManagementService.exceptions.PatientNotFoundException;
import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.Patient;
import com.example.userManagementService.service.AuthService;
import com.example.userManagementService.service.DoctorService;
import com.example.userManagementService.service.JWTService;
import com.example.userManagementService.service.PatientService;
import com.example.userManagementService.feign.AppointmentClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentClient appointmentClient;
    private final DoctorService doctorService;
    private final AuthService authService;
    private final JWTService jwtService;

    @Autowired
    public PatientController(PatientService patientService, AppointmentClient appointmentClient, DoctorService doctorService, AuthService authService, JWTService jwtService) {
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
        this.doctorService = doctorService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long patientId, HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @GetMapping("/search/doctors")
    public ResponseEntity<List<DoctorDTO>> searchDoctorsForPatient(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) Integer experienceYears,
                                                                   @RequestParam(required = false) String specialty){
        List<Doctor> doctors = doctorService.searchDoctorsForPatients(name, experienceYears, specialty);
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(doctor -> new DoctorDTO(
                        doctor.getUser().getFirstName(),
                        doctor.getUser().getLastName(),
                        doctor.getSpecialty(),
                        doctor.getExperienceYears()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorDTOs);
    }

    @PostMapping("/{patientId}/appointments")
    public AppointmentDTO bookAppointment(@PathVariable Long patientId, @RequestBody AppointmentDTO newAppointment, HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctorById(newAppointment.getDoctorId());
        return appointmentClient.createAppointment(patientId, newAppointment);
    }

    @PutMapping("/{patientId}/appointments/{appointmentId}")
    public AppointmentDTO rescheduleAppointment(@PathVariable Long patientId,
                                                @PathVariable Long appointmentId,
                                                @RequestBody CharSequence newDate,
                                                HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return appointmentClient.updateAppointment(patientId, appointmentId, "\"" + newDate + "\"");
    }


    @DeleteMapping("/{patientId}/appointments/{appointmentId}")
    public AppointmentDTO cancelAppointment(@PathVariable Long patientId,
                                            @PathVariable Long appointmentId,
                                            HttpServletRequest request) {

        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new PatientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            return appointmentClient.deleteAppointment(patientId, appointmentId);
        }
    }

    @GetMapping("/{patientId}/appointments")
    public List<AppointmentDTO> getPatientAppointments(@PathVariable Long patientId, HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return appointmentClient.getAppointmentsByPatientId(patientId);
    }

    @GetMapping("/{patientId}/appointments/{appointmentId}")
    public AppointmentDTO getPatientAppointment(@PathVariable Long patientId,
                                                @PathVariable Long appointmentId,
                                                HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return appointmentClient.getPatientAppointment(patientId, appointmentId);
    }
    @GetMapping("/{patientId}/scan-results/{scanId}")
    ResponseEntity<ScanResultDTO> getScanResultById(@PathVariable Long patientId,
                                                    @PathVariable Long scanId,
                                                    HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return appointmentClient.getScanResultById(patientId, scanId);
    }
    @GetMapping("/{patientId}/scan-results")
    ResponseEntity<List<ScanResultDTO>> getScanResultsForPatient(@PathVariable Long patientId,
                                                                 HttpServletRequest request) {
        Long patientIdFromToken = jwtService.extractID(request);
        authService.validateUserAccess(patientIdFromToken, patientId);
        Patient patient = patientService.getPatientById(patientId);
        return appointmentClient.getScanResultsForPatient(patientId);
    }

}

