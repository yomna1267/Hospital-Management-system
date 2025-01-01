package com.example.userManagementService.controller;

import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.exceptions.userNotFoundException;
import com.example.userManagementService.exceptions.doctorNotFoundException;

import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.users;
import com.example.userManagementService.service.adminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/admin")
public class adminController {
    private final adminService adminService;

    public adminController(adminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, String>> addAdmin(@RequestBody users user) {
        users createdUser = adminService.addAdmin(user);
        Map<String, String> response = new HashMap<>();
        response.put("username", createdUser.getUsername());
        response.put("password", createdUser.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAdmin(@PathVariable("id") Long id, @RequestBody users updatedUser) {
        try {
            users updated = adminService.updateAdmin(updatedUser);
            Map<String, String> response = new HashMap<>();
            response.put("username", updated.getUsername());
            response.put("password", updated.getPassword());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (userNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Admin not found");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
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
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<users> getUserById(@PathVariable long id) {
        users user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/")
    public ResponseEntity<List<users>> getAllUsers() {
        List<users> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/username/{userName}")
    public ResponseEntity<users> getUserByUsername(@PathVariable String userName){
        users user = adminService.getUserByUsername(userName);
        return ResponseEntity.ok(user);
    }


    @PostMapping("/doctor")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody doctor newDoctor) {
        doctor createdDoctor = adminService.addDoctor(newDoctor);
        Map<String, String> response = new HashMap<>();
        response.put("username", createdDoctor.getUser().getUsername());
        response.put("password", createdDoctor.getUser().getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/doctor/{id}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody doctor updatedDoctor) {
       try {
           doctor doctor = adminService.updateDoctor(doctorId, updatedDoctor);
           Map<String, String> response = new HashMap<>();
           response.put("username", doctor.getUser().getUsername());
           response.put("password", doctor.getUser().getPassword());

           return ResponseEntity.status(HttpStatus.OK).body(response);
       }
       catch(doctorNotFoundException e) {
           Map<String, String> errorResponse = new HashMap<>();
           errorResponse.put("error", "Doctor not found");
           errorResponse.put("message", e.getMessage());

           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
       }
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

    @PostMapping("/patient")
    public ResponseEntity<Map<String, String>> addPatient(@RequestBody patient newPatient) {
        patient createdPatient = adminService.addPatient(newPatient);
        Map<String, String> response = new HashMap<>();
        response.put("username", createdPatient.getUser().getUsername());
        response.put("password", createdPatient.getUser().getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/patient/{id}")
    public ResponseEntity<Map<String, String>> updatePatient(@PathVariable("id") Long id, @RequestBody patient updatedPatient) {
        try{
            patient updated = adminService.updatePatient(id, updatedPatient);
            Map<String, String> response = new HashMap<>();
            response.put("username", updated.getUser().getUsername());
            response.put("password", updated.getUser().getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (patientNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Patient not found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
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
