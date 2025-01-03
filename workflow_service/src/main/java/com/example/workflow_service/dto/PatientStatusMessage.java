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

    public Long getPatientId() {
        return patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Patient_Events getEvent() {
        return event;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setEvent(Patient_Events event) {
        this.event = event;
    }
}
