package com.example.userManagementService.feign;

import com.example.userManagementService.dto.appointmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "appointment-service", url = "http://localhost:8081", configuration = feignConfig.class)
public interface appointmentClient {
    @PostMapping("/api/patients/{patientId}/appointments")
    appointmentDTO createAppointment(@PathVariable Long patientId, @RequestBody appointmentDTO appointment);

    @PutMapping(value = "/api/patients/{patientId}/appointments/{appointmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    appointmentDTO updateAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId, @RequestBody CharSequence newDate);

    @DeleteMapping("/api/patients/{patientId}/appointments/{appointmentId}")
    appointmentDTO deleteAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments/{appointmentId}")
    appointmentDTO getAppointment(@PathVariable("appointmentId") Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments")
    List<appointmentDTO> getAppointmentsByPatientId(@PathVariable Long patientId);

    @GetMapping("api/doctors/{doctorId}/appointments")
    List<appointmentDTO> getAppointmentsByDoctorId(@PathVariable Long doctorId);

    @GetMapping("api/doctors/{doctorId}/appointments/{appointmentId}")
    appointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId);

    @GetMapping("api/patients/{patientId}/appointments/{appointmentId}")
    appointmentDTO getPatientAppointment(@PathVariable Long patientId, @PathVariable Long appointmentId);
}
