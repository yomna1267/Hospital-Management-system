package com.example.workflow_service.dto;

import com.example.workflow_service.enums.Patient_States;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientStatusDTO {
    private Long id;
    private Long patientId;
    private long appointmentId;
    private Patient_States state;
    private LocalDateTime createdAt;


    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public Patient_States getState() {
        return state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setState(Patient_States state) {
        this.state = state;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
