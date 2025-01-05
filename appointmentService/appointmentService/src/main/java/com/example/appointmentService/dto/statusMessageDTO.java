package com.example.appointmentService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class statusMessageDTO {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private State status;
}
