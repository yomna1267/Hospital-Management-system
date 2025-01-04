package com.example.userManagementService.service;

import com.example.userManagementService.dto.appointmentDTO;
import com.example.userManagementService.dto.patientDTO;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.repository.patientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class patientService {
    private final patientRepository patientRepository;
    @Autowired
    public patientService(patientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public patientDTO mapToPatientDTO(appointmentDTO appointment) {
        Long patientId = appointment.getPatientId();

        patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new patientNotFoundException("Patient with ID " + patientId + " not found"));

        patientDTO patientDto = new patientDTO();
        patientDto.setFirstName(patient.getUser().getFirstName());
        patientDto.setLastName(patient.getUser().getLastName());
        patientDto.setMedicalHistory(patient.getMedicalHistory());
        patientDto.setAge(patient.getUser().getAge());

        return patientDto;
    }

    public patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new patientNotFoundException("Patient with ID " + id + " not found"));
    }
}
