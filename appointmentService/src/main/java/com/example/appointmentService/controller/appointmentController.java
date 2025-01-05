package com.example.appointmentService.controller;



import com.example.appointmentService.models.appointment;
import com.example.appointmentService.service.appointmentServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class appointmentController {
    @Autowired
    private final appointmentServ service;
    @Autowired
    public appointmentController(appointmentServ service) {
        this.service = service;
    }
    @PostMapping("/patients/{patientId}/appointments")
    public appointment createAppointment(
            @PathVariable Long patientId,
            @RequestBody appointment newAppointment) {
        System.out.println("hello from the ctrl");
        newAppointment.setPatientId(patientId);
        return service.createAppointment(newAppointment);
    }

    @PutMapping("/patients/{patientId}/appointments/{appointmentId}")
    public appointment updateAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId,
            @RequestBody CharSequence newDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(newDate, formatter);
        return service.updateAppointmentDate(appointmentId, parsedDate,patientId);

    }

    @DeleteMapping("/patients/{patientId}/appointments/{appointmentId}")
    public appointment deleteAppointment(@PathVariable Long appointmentId, @PathVariable Long patientId) {
        service.deleteAppointment(appointmentId, patientId);
        return service.deleteAppointment(appointmentId, patientId);    }

    @GetMapping("/patients/{patientId}/appointments")
    public List<appointment> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return service.findAppointmentsByPatientId(patientId);
    }
    @GetMapping("/doctors/{doctorId}/appointments")
    public List<appointment> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return service.findAppointmentsByDoctorId(doctorId);
    }

    @GetMapping("/doctors/{doctorId}/appointments/{appointmentId}")
    public ResponseEntity<appointment> getDoctorAppointment(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId) {
        appointment appointment = service.getAppointmentForDoctor(doctorId, appointmentId);
        return ResponseEntity.ok(appointment);
    }

    // Endpoint for patients to view their appointment
    @GetMapping("/patients/{patientId}/appointments/{appointmentId}")
    public ResponseEntity<appointment> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {
        appointment appointment = service.getAppointmentForPatient(patientId, appointmentId);
        return ResponseEntity.ok(appointment);
    }
}