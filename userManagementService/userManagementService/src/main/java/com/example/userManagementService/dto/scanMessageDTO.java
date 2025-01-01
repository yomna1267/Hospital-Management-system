package com.example.userManagementService.dto;

import com.example.userManagementService.enums.patientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class scanMessageDTO {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private patientStatus status;
}
