package com.example.userManagementService.feign;

import com.example.userManagementService.config.FeignConfig;
import com.example.userManagementService.dto.AppointmentDTO;
import com.example.userManagementService.dto.ScanResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "appointment-service",
        url = "http://localhost:8081",
        configuration = FeignConfig.class)
public interface AppointmentClient {
    @PostMapping("/api/patients/{patientId}/appointments")
    AppointmentDTO createAppointment(@PathVariable Long patientId, @RequestBody AppointmentDTO appointment);

    @PutMapping(value = "/api/patients/{patientId}/appointments/{appointmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    AppointmentDTO updateAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId, @RequestBody CharSequence newDate);

    @DeleteMapping("/api/patients/{patientId}/appointments/{appointmentId}")
    AppointmentDTO deleteAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments/{appointmentId}")
    AppointmentDTO getAppointment(@PathVariable("appointmentId") Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments")
    List<AppointmentDTO> getAppointmentsByPatientId(@PathVariable Long patientId);

    @GetMapping("api/doctors/{doctorId}/appointments")
    List<AppointmentDTO> getAppointmentsByDoctorId(@PathVariable Long doctorId);

    @GetMapping("api/doctors/{doctorId}/appointments/{appointmentId}")
    AppointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments/{appointmentId}")
    AppointmentDTO getPatientAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId);

    @GetMapping("api/patients/{patientId}/scan-results")
    ResponseEntity<List<ScanResultDTO>> getScanResultsForPatient(@PathVariable Long patientId);

    @GetMapping("api/patients/{patientId}/scan-results/{scanId}")
    ResponseEntity<ScanResultDTO> getScanResultById(@PathVariable Long patientId, @PathVariable Long scanId);

    @PostMapping("api/doctors/{doctorId}/patients/{patientId}/scan-results")
    ResponseEntity<ScanResultDTO> addScanResult(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody ScanResultDTO scanResultDTO);

}
