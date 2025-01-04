package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.UserNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.role;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.doctorRepository;
import com.example.userManagementService.repository.UserRepository;
import com.example.userManagementService.repository.patientRepository;
import com.example.userManagementService.repository.roleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class adminService {
    private final doctorRepository doctorRepository;
    private final patientRepository patientRepository;
    private final UserRepository userRepository;
    private final roleRepository roleRepository;
    private final workingHoursService workingHoursService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public adminService(doctorRepository doctorRepository, patientRepository patientRepository, UserRepository userRepository, com.example.userManagementService.repository.roleRepository roleRepository, com.example.userManagementService.service.workingHoursService workingHoursService, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.workingHoursService = workingHoursService;
        this.passwordEncoder = passwordEncoder;
    }

    //ADMIN
    @Transactional
    public Map<String,String> addAdmin(Users newUser){
        role adminRole = (role) roleRepository.findByname("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role 'Admin' not found"));
        newUser.setRole(adminRole);
        String password = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(password));
        System.out.println(passwordEncoder.encode(password));
        System.out.println(password);
        //newUser.setPassword(password);
        Users savedUser = userRepository.save(newUser);
        Map<String, String> response = new HashMap<>();
        response.put("username", String.valueOf(newUser.getUsername()));
        response.put("password", password);
        return response;
    }

    @Transactional
    public Users updateAdmin(Users updatedUser) {
        Users existingUser = userRepository.findById(updatedUser.getId()).orElseThrow(() ->new UserNotFoundException("user with id " + updatedUser.getId() + " not found"));
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setGender(updatedUser.getGender());
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
             userRepository.save(existingUser);
        }
        return null;
    }

    @Transactional
    public Users getUserById(long id) {
        Optional<Users> user= userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        else{
            throw new UserNotFoundException("user with ID " + id + " not found");
        }

    }

    @Transactional
    public void deleteAdmin(Users adminToDelete) {
        userRepository.delete(adminToDelete);
    }

    @Transactional
    public List<Users> getAllAdmins() {
        return userRepository.findByRoleName("ADMIN");
    }

    @Transactional
    public List<Users> getAllUsers(){
        return userRepository.findAll();
    }


    //DOCTOR
    @Transactional
    public Map<String,String> addDoctor(doctor newDoctor) {
        role doctorRole = (role) roleRepository.findByname("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Role 'Doctor' not found"));
        newDoctor.getUser().setRole(doctorRole);
        String password = UUID.randomUUID().toString();
        newDoctor.getUser().setPassword(passwordEncoder.encode(password));
        Map<String,String> response = new HashMap<>();
        response.put("username", newDoctor.getUser().getUsername());
        response.put("password", password);
        Users savedUser = userRepository.save(newDoctor.getUser());
        newDoctor.setUser(savedUser);
        newDoctor.setId(savedUser.getId());
        doctor savedDoctor= doctorRepository.save(newDoctor);
        return response;
    }

    @Transactional
    public doctor updateDoctor(Long doctorId, doctor updatedDoctor) {
        doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new doctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        Users existingUser = existingDoctor.getUser();
        Users updatedUser = updatedDoctor.getUser();

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setAge(updatedUser.getAge());

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        existingDoctor.setSpecialty(updatedDoctor.getSpecialty());
        existingDoctor.setExperienceYears(updatedDoctor.getExperienceYears());
        workingHoursService.updateWorkingHours(existingDoctor, updatedDoctor.getWorkingHours());
        return doctorRepository.save(existingDoctor);
    }


    @Transactional
    public void deleteDoctor(doctor deletedDoctor) {
        doctorRepository.delete(deletedDoctor);
    }

    @Transactional
    public List<doctor> getAllDoctor() {
        return doctorRepository.findAll();
    }

    @Transactional
    public doctor getDoctorById(long id) {
        Optional<doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()) {
            return doctor.get();
        } else {
            throw new doctorNotFoundException("doctor with ID " + id + " not found");
        }
    }


    //PATIENT
    @Transactional
    public Map<String,String> addPatient(patient newPatient) {
        role patientRole = (role) roleRepository.findByname("PATIENT")
                .orElseThrow(() -> new RuntimeException("Role 'Patient' not found"));

        newPatient.getUser().setRole(patientRole);
        String password = UUID.randomUUID().toString();
        newPatient.getUser().setPassword(passwordEncoder.encode(password));

        Users savedUser = userRepository.save(newPatient.getUser());
        newPatient.setUser(savedUser);
        newPatient.setId(savedUser.getId());
        patient savedPatient = patientRepository.save(newPatient);

        Map<String, String> response = new HashMap<>();
        response.put("username", newPatient.getUser().getUsername());
        response.put("password", password);
        return response;
    }

    @Transactional
    public patient updatePatient(Long id, patient updatedPatient) {
        patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new patientNotFoundException("Patient with ID " + id + " not found"));

        Users existingUser = existingPatient.getUser();
        Users updatedUser = updatedPatient.getUser();

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setAge(updatedUser.getAge());

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        existingPatient.setMedicalHistory(updatedPatient.getMedicalHistory());

        return patientRepository.save(existingPatient);
    }


    @Transactional
    public void deletePatient(patient deletedPatient) {
        patientRepository.delete(deletedPatient);
    }

    @Transactional
    public List<patient> getAllPatient() {
        return patientRepository.findAll();
    }

    @Transactional
    public patient getPatientById(long id) {
        Optional<patient> patient= patientRepository.findById(id);
        if (patient.isPresent()) {
            return patient.get();
        }
        else{
            throw new patientNotFoundException("patient with ID " + id + " not found");
        }
    }
    @Transactional
    public Users getUserByUsername(String userName) {
        Optional<Users> user= userRepository.findByUsername(userName);
        if (user.isPresent()) {
            return user.get();
        }
        else{
            throw new UserNotFoundException("user with username " + userName + " not found");
        }
    }
}
