package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.repository.doctorRepository;
import org.springframework.data.jpa.domain.Specification;
import com.example.userManagementService.specification.doctorSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class doctorService {
    private doctorRepository doctorRepository;

    public doctorService(doctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
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
    public List<doctor> searchDoctorsForPatients(String name, Integer experienceYears, String specialty) {
            Specification<doctor> specification = doctorSpecification.searchDoctors(name, experienceYears, specialty);
            return doctorRepository.findAll(specification);
    }
}
