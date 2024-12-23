package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.PatientNotFoundException;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.repository.patientRepo;
import com.example.userManagementService.feign.appointmentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class patientServ {
    private final patientRepo patientRepository;
    private final appointmentClient appointmentClient;
    @Autowired
    public patientServ(patientRepo patientRepository, appointmentClient appointmentClient) {
        this.patientRepository = patientRepository;
        this.appointmentClient = appointmentClient;
    }

    @Transactional
    public patient createPatient(patient newPatient) {
        System.out.println("patient created");
        patient savedPatient = patientRepository.save(newPatient);
        return savedPatient;
    }

    public patient getPatientById(Long id) {
        System.out.println("Fetching patient with ID: " + id);
        Optional<patient> patientOpt = patientRepository.findById(id);

        if (patientOpt.isPresent()) {
            return patientOpt.get();
        } else {
            System.err.println("Patient with ID " + id + " not found");
            throw new PatientNotFoundException("Patient with ID " + id + " not found");
        }
    }
}
