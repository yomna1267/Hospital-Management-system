package com.example.appointmentService.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-management-service", url = "http://localhost:8083", configuration = feignConfig.class) // Replace with actual Doctor Service URL
public interface doctorClient {

    @GetMapping("/api/doctor/{doctorId}/availability")
    List<workingHoursDTO> getDoctorAvailability(@PathVariable Long doctorId);
}



