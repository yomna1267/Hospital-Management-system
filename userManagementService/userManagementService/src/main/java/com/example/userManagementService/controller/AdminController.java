package com.example.userManagementService.controller;

import com.example.userManagementService.exceptions.userNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.service.adminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/admin")
@Secured("ROLE_ADMIN")
public class adminController {
    private final adminService adminService;
    private final PasswordEncoder passwordEncoder;

    public adminController(adminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    //Done
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> addAdmin(@RequestBody Users user) {

//        Map<String, String> response = new HashMap<>();
//        response.put("username", createdUser.getUsername());
//        response.put("password", createdUser.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addAdmin(user));
    }

    //Done
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAdmin(@PathVariable("id") Long id, @RequestBody Users updatedUser) {
        try {
            Users updated = adminService.updateAdmin(updatedUser);
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

    //Done
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable long id) {
        Users adminToDelete = adminService.getUserById(id);
        adminService.deleteAdmin(adminToDelete);
        return ResponseEntity.noContent().build();
    }

    //Done
    @GetMapping("/admins")
    public ResponseEntity<List<Users>> getAllAdmins() {
        List<Users> admins = adminService.getAllAdmins();
        //System.out.println("Token received: " + token);
        return ResponseEntity.ok(admins);
    }
    //Done
    @GetMapping("/id/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable long id) {
        Users user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    //Done
    @GetMapping("/")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    //Done
    @GetMapping("/username/{userName}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String userName){
        Users user = adminService.getUserByUsername(userName);
        return ResponseEntity.ok(user);
    }

    //Done
    @PostMapping("/doctor")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody doctor newDoctor) {
//        doctor createdDoctor = adminService.addDoctor(newDoctor);
//        Map<String, String> response = new HashMap<>();
//        response.put("username", createdDoctor.getUser().getUsername());
//        response.put("password", createdDoctor.getUser().getPassword());

        //return ResponseEntity.ok(response);
        return ResponseEntity.ok(adminService.addDoctor(newDoctor));
    }

    @PutMapping("/doctor/{id}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody doctor updatedDoctor) {
        doctor doctor = adminService.updateDoctor(doctorId, updatedDoctor);

        if (doctor != null) {
            Map<String, String> response = new HashMap<>();
            response.put("username", String.valueOf(doctor.getUser().getUsername()));
            response.put("password", doctor.getUser().getPassword());

            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Done
    @DeleteMapping("/doctor/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable long id) {
        doctor doctorToDelete = adminService.getDoctorById(id);
        adminService.deleteDoctor(doctorToDelete);
        return ResponseEntity.noContent().build();
    }

    //done
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

    //Done
    @PostMapping("/patient")
    public ResponseEntity<Map<String, String>> addPatient(@RequestBody patient newPatient) {
        //patient createdPatient = adminService.addPatient(newPatient);
//        Map<String, String> response = new HashMap<>();
//        response.put("username", createdPatient.getUser().getUsername());
//        response.put("password", createdPatient.getUser().getPassword());
//
        return ResponseEntity.ok(adminService.addPatient(newPatient));
    }

    @PutMapping("/patient/{id}")
    public ResponseEntity<Map<String, String>> updatePatient(@PathVariable("id") Long id, @RequestBody patient updatedPatient) {
        patient updated = adminService.updatePatient(id, updatedPatient);

        if (updated != null) {
            Map<String, String> response = new HashMap<>();
            response.put("username", String.valueOf(updated.getUser().getUsername()));
            response.put("password", updated.getUser().getPassword());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
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
