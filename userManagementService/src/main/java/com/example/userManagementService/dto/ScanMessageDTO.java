package com.example.userManagementService.dto;

import com.example.userManagementService.enums.PatientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanMessageDTO {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private PatientStatus status;
}
