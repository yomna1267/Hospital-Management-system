package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.userNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.role;
import com.example.userManagementService.models.users;
import com.example.userManagementService.repository.doctorRepository;
import com.example.userManagementService.repository.userRepository;
import com.example.userManagementService.repository.patientRepository;
import com.example.userManagementService.repository.roleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class adminService {
    private final doctorRepository doctorRepository;
    private final patientRepository patientRepository;
    private final userRepository userRepository;
    private final roleRepository roleRepository;
    private final workingHoursService workingHoursService;

    @Autowired
    public adminService(doctorRepository doctorRepository, patientRepository patientRepository, userRepository userRepository, com.example.userManagementService.repository.roleRepository roleRepository, com.example.userManagementService.service.workingHoursService workingHoursService) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.workingHoursService = workingHoursService;
    }

    //ADMIN
    @Transactional
    public users addAdmin(users newUser){
        role adminRole = (role) roleRepository.findByname("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role 'Admin' not found"));
        newUser.setRole(adminRole);
        users savedUser = userRepository.save(newUser);
        return savedUser;
    }

    @Transactional
    public users updateAdmin(users updatedUser) {
        users existingUser = userRepository.findById(updatedUser.getId()).orElseThrow(() ->new userNotFoundException("user with id " + updatedUser.getId() + " not found"));
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
            return userRepository.save(existingUser);
        }
        return null;
    }

    @Transactional
    public users getUserById(long id) {
        Optional<users> user= userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        else{
            throw new userNotFoundException("user with ID " + id + " not found");
        }

    }

    @Transactional
    public void deleteAdmin(users adminToDelete) {
        userRepository.delete(adminToDelete);
    }

    @Transactional
    public List<users> getAllAdmins() {
        return userRepository.findByRoleName("ADMIN");
    }

    @Transactional
    public List<users> getAllUsers(){
        return userRepository.findAll();
    }


    //DOCTOR
    @Transactional
    public doctor addDoctor(doctor newDoctor) {
        role doctorRole = (role) roleRepository.findByname("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Role 'Doctor' not found"));
        newDoctor.getUser().setRole(doctorRole);
        users savedUser = userRepository.save(newDoctor.getUser());
        newDoctor.setUser(savedUser);
        newDoctor.setId(savedUser.getId());
        doctor savedDoctor= doctorRepository.save(newDoctor);
        return savedDoctor;
    }

    @Transactional
    public doctor updateDoctor(Long doctorId, doctor updatedDoctor) {
        doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new doctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        users existingUser = existingDoctor.getUser();
        users updatedUser = updatedDoctor.getUser();

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
    public patient addPatient(patient newPatient) {
        role patientRole = (role) roleRepository.findByname("PATIENT")
                .orElseThrow(() -> new RuntimeException("Role 'Patient' not found"));
        newPatient.getUser().setRole(patientRole);
        users savedUser = userRepository.save(newPatient.getUser());
        newPatient.setUser(savedUser);
        newPatient.setId(savedUser.getId());
        patient savedPatient = patientRepository.save(newPatient);
        return savedPatient;
    }

    @Transactional
    public patient updatePatient(Long id, patient updatedPatient) {
        patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new patientNotFoundException("Patient with ID " + id + " not found"));

        users existingUser = existingPatient.getUser();
        users updatedUser = updatedPatient.getUser();

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
    public users getUserByUsername(String userName) {
        Optional<users> user= userRepository.findByUsername(userName);
        if (user.isPresent()) {
            return user.get();
        }
        else{
            throw new userNotFoundException("user with username " + userName + " not found");
        }
    }
}
