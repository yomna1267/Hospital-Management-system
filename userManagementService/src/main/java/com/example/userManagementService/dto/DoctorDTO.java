package com.example.userManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private String firstName;
    private String lastName;
    private String specialty;
    private String experienceYears;
}
