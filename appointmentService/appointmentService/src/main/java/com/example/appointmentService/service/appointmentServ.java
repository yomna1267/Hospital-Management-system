package com.example.appointmentService.service;


import com.example.appointmentService.dto.State;
import com.example.appointmentService.dto.statusMessageDTO;
import com.example.appointmentService.exceptions.AppointmentNotFoundException;
import com.example.appointmentService.models.appointment;
import com.example.appointmentService.models.appointmentStatus;
import com.example.appointmentService.repository.appointmentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.appointmentService.feign.doctorClient;
import com.example.appointmentService.feign.workingHoursDTO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class appointmentServ {
    @Autowired
    private appointmentRepo repository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private doctorClient doctorClient;

    private static final String APPOINTMENT_QUEUE = "appointmentQueue";
    private static final String STATUS_QUEUE = "statueQueue";


    private boolean isWithinDoctorAvailability(Long doctorId, LocalDateTime appointmentDate) {
        // Fetch doctor availability
        List<workingHoursDTO> availabilities = doctorClient.getDoctorAvailability(doctorId);

        // Get the day and time of the appointment
        String appointmentDay = appointmentDate.getDayOfWeek().name(); // e.g., MONDAY
        LocalTime appointmentTime = appointmentDate.toLocalTime();

        // Check if the appointment falls within the doctor's availability
        for (workingHoursDTO availability : availabilities) {
            if (availability.getDay().equalsIgnoreCase(appointmentDay)) {
                LocalTime startTime = LocalTime.parse(availability.getStartTime());
                LocalTime endTime = LocalTime.parse(availability.getEndTime());

                if (!appointmentTime.isBefore(startTime) && !appointmentTime.isAfter(endTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public appointment createAppointment(appointment newAppointment) {
        // Validate that the appointment date is not in the past
        if (newAppointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date cannot be in the past");
        }
        // Check doctor's availability
        if (!isWithinDoctorAvailability(newAppointment.getDoctorId(), newAppointment.getAppointmentDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment falls outside of doctor's availability.");
        }
        System.out.println("hi from appointment");
        Optional<appointment> existingAppointment =
                repository.findByDoctorIdAndAppointmentDate(newAppointment.getDoctorId(), newAppointment.getAppointmentDate());

        Optional<appointment> conflict = repository.findByDoctorIdAndTimeRangeCreate(
                newAppointment.getDoctorId(),
                newAppointment.getAppointmentDate().minusMinutes(30),
                newAppointment.getAppointmentDate().plusMinutes(30)
        );
        if (existingAppointment.isPresent()) { //existing appointment is the desired slot
            if (existingAppointment.get().getStatus().equals(appointmentStatus.CANCELLED)) {
                newAppointment.setStatus(appointmentStatus.BOOKED);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment slot is already reserved.");
            }
        } else {
            newAppointment.setStatus(appointmentStatus.BOOKED);
        }

        if (conflict.isPresent()) {
            appointment existAppointment = conflict.get();
            if (existAppointment.getStatus().equals(appointmentStatus.CANCELLED)) {
                newAppointment.setStatus(appointmentStatus.BOOKED);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment slot is already reserved within the unavailable time range (30 min).");
            }
        } else {
            newAppointment.setStatus(appointmentStatus.BOOKED);
        }
        appointment savedappointment = repository.save(newAppointment);
        // Send message to RabbitMQ
        try {
            // Convert AppointmentMessage to JSON
            String messageJson = objectMapper.writeValueAsString(savedappointment);

            // Send the JSON message to RabbitMQ
            rabbitTemplate.convertAndSend(APPOINTMENT_QUEUE, messageJson);
            System.out.println("Message sent: " + messageJson);

            //Send message to rabbitmq status queue
            statusMessageDTO registerMessage = new statusMessageDTO(savedappointment.getPatientId(),savedappointment.getDoctorId(),savedappointment.getId(), State.Register);
            String messageJson2 = objectMapper.writeValueAsString(registerMessage);
            rabbitTemplate.convertAndSend(STATUS_QUEUE, messageJson2);
            System.out.println("Message sent: " + messageJson2);


        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing message", e);
        }

        return savedappointment;
    }

    @Transactional
    public appointment updateAppointmentDate(Long appointmentId, LocalDateTime newDate, Long patientId) {
        // Fetch the existing appointment or throw an exception if not found
        appointment existingAppointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        // trying to reschedule a cancelled appointment
        if (existingAppointment.getStatus().equals(appointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This appointment slot is already cancelled.");
        }
        // Validate that the new date is not in the past
        if (newDate.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New appointment date cannot be in the past");
        }
        // a patient cannot update another patient's appointment
        if (!existingAppointment.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update another patient's appointment.");
        }
        // Check doctor's availability
        if (!isWithinDoctorAvailability(existingAppointment.getDoctorId(), newDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New appointment falls outside of doctor's availability.");
        }


        // conflict on the same appointment slot
        Optional<appointment> conflict = repository.findByDoctorIdAndAppointmentDate(existingAppointment.getDoctorId(), newDate);
        if (conflict.isPresent()) {
            if (conflict.get().getStatus().equals(appointmentStatus.CANCELLED)) {
                existingAppointment.setStatus(appointmentStatus.RESCHEDULED); // Reschedule if the conflict is a cancelled appointment
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment slot is already reserved.");
            }
        } else {
            existingAppointment.setStatus(appointmentStatus.RESCHEDULED);
        }

        // conflict on the 30-minute time range condition
        Optional<appointment> conflictAppointment = repository.findByDoctorIdAndTimeRange(
                existingAppointment.getDoctorId(),
                newDate.minusMinutes(30),
                newDate.plusMinutes(30),
                existingAppointment.getPatientId()
        );
        if (conflictAppointment.isPresent()) {
            appointment conflictingAppointment = conflictAppointment.get();
            // reschedule if the conflicting appointment belongs to the same patient
            if (!conflictingAppointment.getPatientId().equals(existingAppointment.getPatientId())) {
                if (conflictingAppointment.getStatus().equals(appointmentStatus.CANCELLED)) {
                    existingAppointment.setStatus(appointmentStatus.RESCHEDULED); // Reschedule if the conflicting appointment is cancelled
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment slot is already reserved within the unavailable time range (30 min).");
                }
            }
        } else {
            existingAppointment.setStatus(appointmentStatus.RESCHEDULED);
        }

        existingAppointment.setAppointmentDate(newDate);
        appointment savedappointment = repository.save(existingAppointment);
        // Send message to RabbitMQ
        try {
            // Convert AppointmentMessage to JSON
            String messageJson = objectMapper.writeValueAsString(savedappointment);

            // Send the JSON message to RabbitMQ
            rabbitTemplate.convertAndSend(APPOINTMENT_QUEUE, messageJson);
            System.out.println("Message sent: " + messageJson);

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing message", e);
        }
        return savedappointment;
    }


    public appointment deleteAppointment(Long appointmentId, Long patientId) {
        appointment cancelledAppointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));
        cancelledAppointment.setStatus(appointmentStatus.CANCELLED);

        // a patient cannot delete another patient's appointment
        if (!cancelledAppointment.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete another patient's appointment.");
        }
        appointment savedappointment = repository.save(cancelledAppointment);
        // Send message to RabbitMQ
        try {
            // Convert AppointmentMessage to JSON
            String messageJson = objectMapper.writeValueAsString(savedappointment);

            // Send the JSON message to RabbitMQ
            rabbitTemplate.convertAndSend(APPOINTMENT_QUEUE, messageJson);
            System.out.println("Message sent: " + messageJson);
            //Send message to rabbitmq status queue
            statusMessageDTO registerMessage = new statusMessageDTO(savedappointment.getPatientId(),savedappointment.getDoctorId(),savedappointment.getId(), State.APPOINTMENT_CANCELLED);
            String messageJson2 = objectMapper.writeValueAsString(registerMessage);
            rabbitTemplate.convertAndSend(STATUS_QUEUE, messageJson2);
            System.out.println("Message sent: " + messageJson2);

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing message", e);
        }
        return savedappointment;
    }


    public appointment getAppointmentDetails(Long appointmentId) {
        Optional<appointment> appointment = repository.findById(appointmentId);

        // Return the appointment if found, else return null
        return appointment.orElse(null);
    }

    public List<appointment> findAppointmentsByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }
    public List<appointment> findAppointmentsByDoctorId(Long doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    public appointment getAppointmentForDoctor(Long doctorId, Long appointmentId) {
        return repository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for doctor with ID: " + doctorId));
    }

    public appointment getAppointmentForPatient(Long patientId, Long appointmentId) {
        return repository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for patient with ID: " + patientId));
    }
}
