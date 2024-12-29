package com.example.userManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class scanResultDTO {
    private Long id;
    private String type;
    private String results;
    private Long patientId;
    private Long doctorId;
    private appointmentDTO appointment;
}
