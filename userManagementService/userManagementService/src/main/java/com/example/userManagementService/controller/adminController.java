package com.example.userManagementService.controller;

import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.userNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.users;
import com.example.userManagementService.service.adminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/admin")
@Secured("ROLE_ADMIN")
public class adminController {
    private final adminService adminService;

    public adminController(adminService adminService) {
        this.adminService = adminService;
    }

    //ADMIN
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> addAdmin(@RequestBody users user) {

        Map<String,String> response = adminService.addAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAdmin(@PathVariable("id") Long id, @RequestBody users updatedUser) {
        users updated = adminService.updateAdmin(id, updatedUser);
        Map<String, String> response = new HashMap<>();
        response.put("username", updated.getUsername());
        response.put("password", updated.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable long id) {
        users adminToDelete = adminService.getUserById(id);
        adminService.deleteAdmin(adminToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admins")
    public ResponseEntity<List<users>> getAllAdmins() {
        List<users> admins = adminService.getAllAdmins();
        return ResponseEntity.status(HttpStatus.OK).body(admins);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<users> getUserById(@PathVariable long id) {
        users user = adminService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/")
    public ResponseEntity<List<users>> getAllUsers() {
        List<users> users = adminService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<users> getUserByUsername(@PathVariable String userName){
        users user = adminService.getUserByUsername(userName);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    //DOCTOR
    @PostMapping("/doctor")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody doctor newDoctor) {
        Map<String, String> response = adminService.addDoctor(newDoctor);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/doctor/{id}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody doctor updatedDoctor) {
        doctor doctor = adminService.updateDoctor(doctorId, updatedDoctor);
        Map<String, String> response = new HashMap<>();
        response.put("username", String.valueOf(doctor.getUser().getUsername()));
        response.put("password", doctor.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
        return ResponseEntity.status(HttpStatus.OK).body(doctors);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<doctor> getDoctorById(@PathVariable long id) {
        doctor doctor = adminService.getDoctorById(id);
        return ResponseEntity.status(HttpStatus.OK).body(doctor);
    }


    //PATIENT
    @PostMapping("/patient")
    public ResponseEntity<Map<String, String>> addPatient(@RequestBody patient newPatient) {
        Map<String, String> response = adminService.addPatient(newPatient);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/patient/{id}")
    public ResponseEntity<Map<String, String>> updatePatient(@PathVariable("id") Long id, @RequestBody patient updatedPatient) {
         patient updated = adminService.updatePatient(id, updatedPatient);
         Map<String, String> response = new HashMap<>();
         response.put("username", String.valueOf(updated.getUser().getUsername()));
         response.put("password", updated.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<patient> getPatientById(@PathVariable long id) {
        patient patient = adminService.getPatientById(id);
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

}
