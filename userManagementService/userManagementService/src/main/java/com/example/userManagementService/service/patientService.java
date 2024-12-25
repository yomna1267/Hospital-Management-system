package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.feign.appointmentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class patientService {
    private final com.example.userManagementService.repository.patientRepository patientRepository;
    private final appointmentClient appointmentClient;
    @Autowired
    public patientService(com.example.userManagementService.repository.patientRepository patientRepository, appointmentClient appointmentClient) {
        this.patientRepository = patientRepository;
        this.appointmentClient = appointmentClient;
    }

    public patient getPatientById(Long id) {
        System.out.println("Fetching patient with ID: " + id);
        Optional<patient> patientOpt = patientRepository.findById(id);

        if (patientOpt.isPresent()) {
            return patientOpt.get();
        } else {
            System.err.println("Patient with ID " + id + " not found");
            throw new patientNotFoundException("Patient with ID " + id + " not found");
        }
    }
}
