package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.adminNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.models.users;
import com.example.userManagementService.repository.doctorRepository;
import com.example.userManagementService.repository.patientRepo;
import com.example.userManagementService.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class adminService {
    private final doctorRepository doctorRepository;
    private final patientRepo patientRepository;
    private final userRepository userRepository;

    @Autowired
    public adminService(doctorRepository doctorRepository, patientRepo patientRepository, com.example.userManagementService.repository.userRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public users addAdmin(users newUser){
        users savedUser = userRepository.save(newUser);
        return savedUser;
    }

    @Transactional
    public users updateAdmin(users updatedUser){
        users savedUser = userRepository.save(updatedUser);
        return savedUser;
    }

    @Transactional
    public users getAdminById(long id) {
        Optional<users> admins= userRepository.findById(id);
        if (admins.isPresent()) {
            return admins.get();
        }
        else{
            System.err.println("admin with ID " + id + " not found");
            throw new adminNotFoundException("admin with ID " + id + " not found");
        }

    }

    @Transactional
    public void deleteAdmin(users adminToDelete) {
        userRepository.delete(adminToDelete);
    }

    @Transactional
    public List<users> getAllAdmins() {
        return userRepository.findAll();
    }

    @Transactional
    public doctor addDoctor(doctor newDoctor) {
        users savedUser = userRepository.save(newDoctor.getUser());
        newDoctor.setUser(savedUser);
        doctor savedDoctor= doctorRepository.save(newDoctor);
        return savedDoctor;
    }

    @Transactional
    public doctor updateDoctor(doctor updatedDoctor) {
        users savedUser = userRepository.save(updatedDoctor.getUser());
        updatedDoctor.setUser(savedUser);
        doctor savedDoctor= doctorRepository.save(updatedDoctor);
        return savedDoctor;
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
            System.err.println("doctor with ID " + id + " not found");
            throw new doctorNotFoundException("doctor with ID " + id + " not found");
        }
    }

    @Transactional
    public patient addPatient(patient newPatient) {
        users savedUser = userRepository.save(newPatient.getUser());
        newPatient.setUser(savedUser);
        patient savedPatient = patientRepository.save(newPatient);
        return savedPatient;
    }

    @Transactional
    public patient updatePatient(patient updatedPatient) {
        users savedUser = userRepository.save(updatedPatient.getUser());
        updatedPatient.setUser(savedUser);
        patient savedPatient = patientRepository.save(updatedPatient);
        return savedPatient;
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
            System.err.println("patient with ID " + id + " not found");
            throw new patientNotFoundException("patient with ID " + id + " not found");
        }
    }

}
