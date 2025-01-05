package com.example.userManagementService.service;

import com.example.userManagementService.dto.AppointmentDTO;
import com.example.userManagementService.dto.PatientDTO;
import com.example.userManagementService.exceptions.PatientNotFoundException;
import com.example.userManagementService.models.Patient;
import com.example.userManagementService.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PatientService {
    private final PatientRepository patientRepository;
    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientDTO mapToPatientDTO(AppointmentDTO appointment) {
        Long patientId = appointment.getPatientId();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + patientId + " not found"));

        PatientDTO patientDto = new PatientDTO();
        patientDto.setFirstName(patient.getUser().getFirstName());
        patientDto.setLastName(patient.getUser().getLastName());
        patientDto.setMedicalHistory(patient.getMedicalHistory());
        patientDto.setAge(patient.getUser().getAge());

        return patientDto;
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found"));
    }
}
