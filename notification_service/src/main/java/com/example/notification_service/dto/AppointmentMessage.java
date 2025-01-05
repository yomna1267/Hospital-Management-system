package com.example.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentMessage {
    private Long Id;
    private Long patientId; //in users table
    private Long doctorId;
    private appointmentStatus status;  // BOOKED, RESCHEDULED, CANCELLED
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public appointmentStatus getStatus() {
        return status;
    }

    public Long getId() {
        return Id;
    }

    public void setStatus(appointmentStatus status) {
        this.status = status;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

}
