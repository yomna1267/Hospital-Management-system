package com.example.userManagementService.service;

import com.example.userManagementService.dto.AppointmentDTO;
import com.example.userManagementService.exceptions.AppointmentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private RestTemplate restTemplate;

    public AppointmentDTO getAppointment(Long patientId, Long appointmentId) {
        try {
            String url = "http://localhost:8081/api/patients/{patientId}/appointments/{appointmentId}";

            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("patientId", patientId);
            uriVariables.put("appointmentId", appointmentId);

            ResponseEntity<AppointmentDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    AppointmentDTO.class,
                    uriVariables
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found for this patient.");
        }
    }

}
