package com.example.appointmentService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-management-service", url = "http://localhost:8083")
public interface doctorClient {

    @GetMapping("/api/doctor/{doctorId}/availability")
    List<workingHoursDTO> getDoctorAvailability(@PathVariable Long doctorId);
}



