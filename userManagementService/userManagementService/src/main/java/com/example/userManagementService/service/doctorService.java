package com.example.userManagementService.service;

import com.example.userManagementService.dto.scanMessageDTO;
import com.example.userManagementService.dto.workingHoursDTO;
import com.example.userManagementService.enums.patientStatus;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.workingHours;
import com.example.userManagementService.repository.doctorRepository;
import com.example.userManagementService.repository.workingHoursRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.example.userManagementService.specification.doctorSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class doctorService {
    private final workingHoursRepository workingHoursRepository;
    private final doctorRepository doctorRepository;
    private  final workingHoursService workingHoursService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STATUS_QUEUE = "statusQueue";

    public doctorService(doctorRepository doctorRepository, workingHoursRepository workingHoursRepository, workingHoursService workingHoursService) {
        this.doctorRepository = doctorRepository;
        this.workingHoursRepository = workingHoursRepository;
        this.workingHoursService = workingHoursService;
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
    @Transactional
    public ResponseEntity<List<workingHoursDTO>> getAvailability(Long id) {
        List<workingHours> doctorAvailability = workingHoursRepository.findByDoctorId(id);
        List<workingHoursDTO> workingHoursDTOList = new ArrayList<>();
        for(workingHours workingHours : doctorAvailability) {
            workingHoursDTO workingHoursDTO = workingHoursService.mapToWorkingHoursDTO(workingHours);
            workingHoursDTOList.add(workingHoursDTO);
        }
        return ResponseEntity.ok(workingHoursDTOList);
    }

    public String dischargePatient(Long patientId,
                                   Long doctorId,
                                   scanMessageDTO scanMessage) {
        Long appointmentId = scanMessage.getAppointmentId();
        scanMessageDTO scanMessageDTO = new scanMessageDTO();
        scanMessageDTO.setDoctorId(doctorId);
        scanMessageDTO.setPatientId(patientId);
        scanMessageDTO.setAppointmentId(appointmentId);
        scanMessageDTO.setStatus(patientStatus.DISCHARGED);

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
