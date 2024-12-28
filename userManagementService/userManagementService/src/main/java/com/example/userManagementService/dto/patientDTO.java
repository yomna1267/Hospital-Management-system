package com.example.userManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class patientDTO {
    private String firstName;
    private String lastName;
    private Integer age;
    private String medicalHistory;
}