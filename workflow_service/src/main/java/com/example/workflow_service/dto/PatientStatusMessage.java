package com.example.workflow_service.dto;

import com.example.workflow_service.enums.Patient_Events;
import com.example.workflow_service.enums.Patient_States;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientStatusMessage {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private Patient_Events event;
}
