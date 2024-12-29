package com.example.userManagementService.service;

import com.example.userManagementService.dto.workingHoursDTO;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.workingHours;
import com.example.userManagementService.repository.doctorRepository;
import com.example.userManagementService.repository.workingHoursRepository;
import org.springframework.data.jpa.domain.Specification;
import com.example.userManagementService.specification.doctorSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class doctorService {
    private final workingHoursRepository workingHoursRepository;
    private doctorRepository doctorRepository;

    public doctorService(doctorRepository doctorRepository, workingHoursRepository workingHoursRepository) {
        this.doctorRepository = doctorRepository;
        this.workingHoursRepository = workingHoursRepository;
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

    public ResponseEntity<List<workingHoursDTO>> getAvailability(Long id) {
        List<workingHours> doctorAvailability = workingHoursRepository.findByDoctorId(id);
        List<workingHoursDTO> workingHoursDTOList = new ArrayList<>();
        for(workingHours workingHours : doctorAvailability) {
            workingHoursDTO workingHoursDTO = mapToWorkingHoursDTO(workingHours);
            workingHoursDTOList.add(workingHoursDTO);
        }
        return ResponseEntity.ok(workingHoursDTOList);
    }

    public workingHoursDTO mapToWorkingHoursDTO(workingHours workingHours) {
        return new workingHoursDTO(
                workingHours.getDay(),
                workingHours.getStartTime(),
                workingHours.getEndTime()
        );
    }
}
