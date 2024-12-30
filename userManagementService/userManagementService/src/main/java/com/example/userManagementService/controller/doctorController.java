package com.example.userManagementService.controller;

import com.example.userManagementService.dto.*;
import com.example.userManagementService.enums.patientStatus;
import com.example.userManagementService.exceptions.appointmentNotFoundException;
import com.example.userManagementService.exceptions.doctorNotFoundException;
import com.example.userManagementService.exceptions.patientNotFoundException;
import com.example.userManagementService.feign.appointmentClient;
import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.patient;
import com.example.userManagementService.repository.patientRepository;
import com.example.userManagementService.service.appointmentService;
import com.example.userManagementService.service.doctorService;
import com.example.userManagementService.service.patientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/doctor")
public class doctorController {
    private final doctorService doctorService;
    private final patientService patientService;
    private final appointmentClient appointmentClient;
    private final appointmentService appointmentService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STATUS_QUEUE = "statusQueue";
    public doctorController(doctorService doctorService, patientService patientService, appointmentClient appointmentClient, appointmentService appointmentService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<doctor> getDoctorById(@PathVariable Long id) {
        try {
            doctor doctor = doctorService.getDoctorById(id);
            return new ResponseEntity<>(doctor, HttpStatus.OK);
        } catch (patientNotFoundException ex) {
            throw new doctorNotFoundException("doctor with ID " + id + " not found");
        }
    }

    @GetMapping("/{doctorId}/appointments")
    public List<appointmentDTO> getDoctorAppointments(@PathVariable Long doctorId) {
        doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else {
             return appointmentClient.getAppointmentsByDoctorId(doctorId);
        }
    }

    @GetMapping("/doctors/{doctorId}/appointments/{appointmentId}")
    public appointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId){
        doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else {
            return appointmentClient.getDoctorAppointment(doctorId, appointmentId);
        }
    }

    @GetMapping("/{doctorId}/search/{patientId}")
    public patientDTO searchPatientById(@PathVariable Long doctorId, @PathVariable Long patientId) {
        doctor doctor = doctorService.getDoctorById(doctorId);
        patient patient = patientService.getPatientById(patientId);
        if (doctor == null){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else if(patient == null){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else {
            List<appointmentDTO> doctorAppointments = getDoctorAppointments(doctorId);
            appointmentDTO patientAppointment = doctorAppointments
                    .stream()
                    .filter(appointment -> appointment.getPatientId().equals(patientId))
                    .findFirst()
                    .orElseThrow(() -> new appointmentNotFoundException("No appointment found for patient with ID: " + patientId));
            patientDTO patientdto = patientService.mapToPatientDTO(patientAppointment);
            return patientdto;
        }
    }

    @PostMapping("/{doctorId}/patient/{patientId}/scan-results")
    public ResponseEntity<scanResultDTO> addScanResult(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody scanResultDTO scanResultDTO){
        doctor doctor = doctorService.getDoctorById(doctorId);
        patient patient = patientService.getPatientById(patientId);
        if (doctor == null){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else if(patient == null){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else{
            ResponseEntity<scanResultDTO> scanResult = appointmentClient.addScanResult(doctorId, patientId, scanResultDTO);
            Long appointmentId = Objects.requireNonNull(scanResult.getBody()).getAppointment().getId();
            scanMessageDTO scanMessageDTO = new scanMessageDTO();
            scanMessageDTO.setDoctorId(doctorId);
            scanMessageDTO.setPatientId(patientId);
            scanMessageDTO.setAppointmentId(appointmentId);
            scanMessageDTO.setStatus(patientStatus.UNDERTREATMENT);
            // Send message to RabbitMQ
            try {

                String messageJson = objectMapper.writeValueAsString(scanMessageDTO);
                rabbitTemplate.convertAndSend(STATUS_QUEUE, messageJson);
                System.out.println("Message sent: " + messageJson);

            } catch (JsonProcessingException  e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing message", e);
            }
            return new ResponseEntity<>(scanResult.getBody(), HttpStatus.CREATED);
        }

    }

    @PostMapping("/{doctorId}/patient/{patientId}/discharge")
    public ResponseEntity<String> dischargePatient(@PathVariable Long doctorId,
                                                   @PathVariable Long patientId,
                                                   @RequestBody scanMessageDTO scanMessageDTO) {
        doctor doctor = doctorService.getDoctorById(doctorId);
        patient patient = patientService.getPatientById(patientId);
        if (doctor == null){
            throw new doctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        else if(patient == null){
            throw new patientNotFoundException("Patient with ID " + patientId + " not found");
        }
        else{
            try {
                appointmentDTO appointmentDTO = appointmentService.getAppointment(patientId, scanMessageDTO.getAppointmentId());
                if (Objects.equals(appointmentDTO.getDoctorId(), doctorId)) {
                    scanMessageDTO.setPatientId(patientId);
                    scanMessageDTO.setDoctorId(doctorId);
                    String message = doctorService.dischargePatient(patientId, doctorId, scanMessageDTO);
                    return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity<>("Appointment with ID " + scanMessageDTO.getAppointmentId()
                            + " does not belong to doctor with ID " + doctorId, HttpStatus.FORBIDDEN);
                }
            } catch (appointmentNotFoundException e) {
                throw e;
            }
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<workingHoursDTO>> getAvailability(@PathVariable Long id){
        doctor doctor = doctorService.getDoctorById(id);
        if (doctor == null) {
            throw new doctorNotFoundException("Doctor with ID " + id + " not found");
        }
        return doctorService.getAvailability(id);
    }

}
