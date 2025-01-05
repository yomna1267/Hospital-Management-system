package com.example.userManagementService.service;

import com.example.userManagementService.dto.ScanMessageDTO;
import com.example.userManagementService.dto.WorkingHoursDTO;
import com.example.userManagementService.enums.PatientStatus;
import com.example.userManagementService.exceptions.DoctorNotFoundException;
import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.WorkingHours;
import com.example.userManagementService.repository.DoctorRepository;
import com.example.userManagementService.repository.WorkingHoursRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.example.userManagementService.specification.DoctorSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {
    private final WorkingHoursRepository workingHoursRepository;
    private final DoctorRepository doctorRepository;
    private  final WorkingHoursService workingHoursService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STATUS_QUEUE = "statusQueue";

    public DoctorService(DoctorRepository doctorRepository, WorkingHoursRepository workingHoursRepository, WorkingHoursService workingHoursService) {
        this.doctorRepository = doctorRepository;
        this.workingHoursRepository = workingHoursRepository;
        this.workingHoursService = workingHoursService;
    }
    @Transactional
    public Doctor getDoctorById(long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + id + " not found"));
    }
    @Transactional
    public List<Doctor> searchDoctorsForPatients(String name, Integer experienceYears, String specialty) {
            Specification<Doctor> specification = DoctorSpecification.searchDoctors(name, experienceYears, specialty);
            return doctorRepository.findAll(specification);
    }
    @Transactional
    public ResponseEntity<List<WorkingHoursDTO>> getAvailability(Long id) {
        List<WorkingHours> doctorAvailability = workingHoursRepository.findByDoctorId(id);
        List<WorkingHoursDTO> workingHoursDTOList = new ArrayList<>();
        for(WorkingHours workingHours : doctorAvailability) {
            WorkingHoursDTO workingHoursDTO = workingHoursService.mapToWorkingHoursDTO(workingHours);
            workingHoursDTOList.add(workingHoursDTO);
        }
        return ResponseEntity.ok(workingHoursDTOList);
    }

    public String dischargePatient(Long patientId,
                                   Long doctorId,
                                   ScanMessageDTO scanMessage) {
        Long appointmentId = scanMessage.getAppointmentId();
        ScanMessageDTO scanMessageDTO = new ScanMessageDTO();
        scanMessageDTO.setDoctorId(doctorId);
        scanMessageDTO.setPatientId(patientId);
        scanMessageDTO.setAppointmentId(appointmentId);
        scanMessageDTO.setStatus(PatientStatus.TREATMENT_COMPLETED);

        // Send message to RabbitMQ
        try {
            String messageJson = objectMapper.writeValueAsString(scanMessageDTO);
            // Send the JSON message to RabbitMQ
            rabbitTemplate.convertAndSend(STATUS_QUEUE, messageJson);
            System.out.println("Message sent: " + messageJson);
            String message = "Patient with ID " + patientId + " has been successfully discharged by doctor with ID " + doctorId;
            return message;

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing message", e);
        }
    }
}
