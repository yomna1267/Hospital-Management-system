package com.example.userManagementService.controller;

import com.example.userManagementService.dto.*;
import com.example.userManagementService.enums.PatientStatus;
import com.example.userManagementService.exceptions.AppointmentNotFoundException;
import com.example.userManagementService.feign.AppointmentClient;
import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.Patient;
import com.example.userManagementService.service.JWTService;
import com.example.userManagementService.service.AppointmentService;
import com.example.userManagementService.service.DoctorService;
import com.example.userManagementService.service.PatientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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
public class DoctorController {
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentClient appointmentClient;
    private final AppointmentService appointmentService;
    private final JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STATUS_QUEUE = "statusQueue";

    public DoctorController(DoctorService doctorService, PatientService patientService, AppointmentClient appointmentClient, AppointmentService appointmentService, JWTService jwtService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentClient = appointmentClient;
        this.appointmentService = appointmentService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id, HttpServletRequest request) {

        Doctor doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @GetMapping("/{doctorId}/appointments")
    public List<AppointmentDTO> getDoctorAppointments(@PathVariable Long doctorId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        return appointmentClient.getAppointmentsByDoctorId(doctorId);
    }

    @GetMapping("/doctors/{doctorId}/appointments/{appointmentId}")
    public AppointmentDTO getDoctorAppointment(@PathVariable Long doctorId, @PathVariable Long appointmentId){
        Doctor doctor = doctorService.getDoctorById(doctorId);
        return appointmentClient.getDoctorAppointment(doctorId, appointmentId);
    }

    @GetMapping("/{doctorId}/search/{patientId}")
    public PatientDTO searchPatientById(@PathVariable Long doctorId, @PathVariable Long patientId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        Patient patient = patientService.getPatientById(patientId);
        List<AppointmentDTO> doctorAppointments = getDoctorAppointments(doctorId);
        AppointmentDTO patientAppointment = doctorAppointments
                .stream()
                .filter(appointment -> appointment.getPatientId().equals(patientId))
                .findFirst()
                .orElseThrow(() -> new AppointmentNotFoundException("No appointment found for patient with ID: " + patientId));
        PatientDTO patientdto = patientService.mapToPatientDTO(patientAppointment);
        return patientdto;
    }

    @PostMapping("/{doctorId}/patient/{patientId}/scan-results")
    public ResponseEntity<ScanResultDTO> addScanResult(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody ScanResultDTO scanResultDTO){
        Doctor doctor = doctorService.getDoctorById(doctorId);
        Patient patient = patientService.getPatientById(patientId);
        ResponseEntity<ScanResultDTO> scanResult = appointmentClient.addScanResult(doctorId, patientId, scanResultDTO);
        Long appointmentId = Objects.requireNonNull(scanResult.getBody()).getAppointment().getId();
        //Prepare message
        ScanMessageDTO scanMessageDTO = new ScanMessageDTO();
        scanMessageDTO.setDoctorId(doctorId);
        scanMessageDTO.setPatientId(patientId);
        scanMessageDTO.setAppointmentId(appointmentId);
        scanMessageDTO.setStatus(PatientStatus.UNDERTREATMENT);

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


    @PostMapping("/{doctorId}/patient/{patientId}/discharge")
    public ResponseEntity<String> dischargePatient(@PathVariable Long doctorId,
                                                   @PathVariable Long patientId,
                                                   @RequestBody ScanMessageDTO scanMessageDTO) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        Patient patient = patientService.getPatientById(patientId);
        AppointmentDTO appointmentDTO = appointmentService.getAppointment(patientId, scanMessageDTO.getAppointmentId());
        if (Objects.equals(appointmentDTO.getDoctorId(), doctorId)) {
            scanMessageDTO.setPatientId(patientId);
            scanMessageDTO.setDoctorId(doctorId);
            String message = doctorService.dischargePatient(patientId, doctorId, scanMessageDTO);
            return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
        } else {
            throw new AppointmentNotFoundException(
                    "Appointment with ID " + scanMessageDTO.getAppointmentId()
                            + " does not belong to doctor with ID " + doctorId);
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<WorkingHoursDTO>> getAvailability(@PathVariable Long id){
        Doctor doctor = doctorService.getDoctorById(id);
        return doctorService.getAvailability(id);
    }

}
