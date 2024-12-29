package com.example.userManagementService.service;

import com.example.userManagementService.models.doctor;
import com.example.userManagementService.models.workingHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.userManagementService.repository.workingHoursRepository;

import java.util.List;

@Service
public class workingHoursService {
    @Autowired
    private workingHoursRepository workingHoursRepository;

    public List<workingHours> updateWorkingHours(doctor existingDoctor, List<workingHours> updatedWorkingHours) {

        existingDoctor.getWorkingHours().clear();

        for (workingHours wh : updatedWorkingHours) {
            workingHours existingWh = new workingHours();
            existingWh.setDay(wh.getDay());
            existingWh.setStartTime(wh.getStartTime());
            existingWh.setEndTime(wh.getEndTime());
            existingWh.setDoctor(existingDoctor);

            existingDoctor.getWorkingHours().add(existingWh);
        }

        return workingHoursRepository.saveAll(existingDoctor.getWorkingHours());
    }


}
