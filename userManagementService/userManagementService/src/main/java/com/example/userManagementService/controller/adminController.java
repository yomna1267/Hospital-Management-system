package com.example.userManagementService.controller;

import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.users;
import com.example.userManagementService.service.adminService;
import com.example.userManagementService.service.doctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/admin")
public class adminController {
    private final adminService adminService;

    public adminController(adminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/")
    public ResponseEntity<users> addAdmin(@RequestBody users user) {
        users createdUser = adminService.addAdmin(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/")
    public ResponseEntity<users> updateAdmin(@RequestBody users user) {
        users updated = adminService.updateAdmin(user);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable long id) {
        users adminToDelete = adminService.getAdminById(id);
        adminService.deleteAdmin(adminToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<users>> getAllAdmins() {
        List<users> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<users> getAdminById(@PathVariable long id) {
        users admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }


    @PostMapping("/doctor")
    public ResponseEntity<doctor> addDoctor(@RequestBody doctor newDoctor) {
        doctor createdDoctor = adminService.addDoctor(newDoctor);
        return ResponseEntity.ok(createdDoctor);
    }

    @PutMapping("/doctor")
    public ResponseEntity<doctor> updateDoctor(@RequestBody doctor updatedDoctor) {
        doctor updated = adminService.updateDoctor(updatedDoctor);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/doctor/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable long id) {
        doctor doctorToDelete = adminService.getDoctorById(id);
        adminService.deleteDoctor(doctorToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<doctor>> getAllDoctors() {
        List<doctor> doctors = adminService.getAllDoctor();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<doctor> getDoctorById(@PathVariable long id) {
        doctor doctor = adminService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    // Patient Management Endpoints
    @PostMapping("/patient")
    public ResponseEntity<patient> addPatient(@RequestBody patient newPatient) {
        patient createdPatient = adminService.addPatient(newPatient);
        return ResponseEntity.ok(createdPatient);
    }

    @PutMapping("/patient")
    public ResponseEntity<patient> updatePatient(@RequestBody patient updatedPatient) {
        patient updated = adminService.updatePatient(updatedPatient);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/patient/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable long id) {
        patient patientToDelete = adminService.getPatientById(id);
        adminService.deletePatient(patientToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient")
    public ResponseEntity<List<patient>> getAllPatients() {
        List<patient> patients = adminService.getAllPatient();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<patient> getPatientById(@PathVariable long id) {
        patient patient = adminService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
}
