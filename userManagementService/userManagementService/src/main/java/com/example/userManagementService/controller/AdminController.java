package com.example.userManagementService.controller;

import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.Patient;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.service.AdminService;
import com.example.userManagementService.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/admin")
@Secured("ROLE_ADMIN")
public class AdminController {
    private final AdminService adminService;
    public final JWTService jwtService;

    public AdminController(AdminService adminService, JWTService jwtService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    //ADMIN
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> addAdmin(@RequestBody Users user) {
        Map<String,String> response = adminService.addAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAdmin(@PathVariable("id") Long id, @RequestBody Users updatedUser) {
        Map<String, String> response = adminService.updateAdmin(id, updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAdmin(@PathVariable long id) {
//        Users adminToDelete = adminService.getUserById(id);
//        adminService.deleteAdmin(adminToDelete);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/admins")
    public ResponseEntity<List<Users>> getAllAdmins() {
        List<Users> admins = adminService.getAllAdmins();
        return ResponseEntity.status(HttpStatus.OK).body(admins);
    }

//    @GetMapping("/id/{id}")
//    public ResponseEntity<Users> getUserById(@PathVariable long id) {
//        Users user = adminService.getUserById(id);
//        return ResponseEntity.status(HttpStatus.OK).body(user);
//    }
    @GetMapping("/id")
    public ResponseEntity<Users> getUserById(HttpServletRequest request) {
        long id = Long.valueOf(jwtService.extractID(request));
        System.out.println(id);
        Users user = adminService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = adminService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/username")
    public ResponseEntity<Users> getUserByUsername(HttpServletRequest request){
        String userName = jwtService.extractUsername(request);
        Users user = adminService.getUserByUsername(userName);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    //DOCTOR
    @PostMapping("/doctor")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody Doctor newDoctor) {
        Map<String, String> response = adminService.addDoctor(newDoctor);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/doctor/{id}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody Doctor updatedDoctor) {
        Map<String, String> response = adminService.updateDoctor(doctorId, updatedDoctor);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/doctor/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable long id) {
        Doctor doctorToDelete = adminService.getDoctorById(id);
        adminService.deleteDoctor(doctorToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = adminService.getAllDoctor();
        return ResponseEntity.status(HttpStatus.OK).body(doctors);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable long id) {
        Doctor doctor = adminService.getDoctorById(id);
        return ResponseEntity.status(HttpStatus.OK).body(doctor);
    }


    //PATIENT
    @PostMapping("/patient")
    public ResponseEntity<Map<String, String>> addPatient(@RequestBody Patient newPatient) {
        Map<String, String> response = adminService.addPatient(newPatient);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/patient/{id}")
    public ResponseEntity<Map<String, String>> updatePatient(@PathVariable("id") Long id, @RequestBody Patient updatedPatient) {
         Map<String, String> response = adminService.updatePatient(id, updatedPatient);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/patient/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable long id) {
        Patient patientToDelete = adminService.getPatientById(id);
        adminService.deletePatient(patientToDelete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = adminService.getAllPatient();
        return ResponseEntity.status(HttpStatus.OK).body(patients);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable long id) {
        Patient patient = adminService.getPatientById(id);
        return ResponseEntity.status(HttpStatus.OK).body(patient);

    }

}
