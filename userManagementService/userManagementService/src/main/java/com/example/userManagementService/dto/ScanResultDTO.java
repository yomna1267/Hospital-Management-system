package com.example.userManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanResultDTO {
    private Long id;
    private String type;
    private String results;
    private Long patientId;
    private Long doctorId;
    private AppointmentDTO appointment;
}
