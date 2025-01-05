package com.example.userManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursDTO {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
}
