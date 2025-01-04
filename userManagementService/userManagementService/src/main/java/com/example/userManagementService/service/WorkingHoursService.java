package com.example.userManagementService.service;

import com.example.userManagementService.dto.WorkingHoursDTO;
import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.WorkingHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.userManagementService.repository.WorkingHoursRepository;

import java.util.List;

@Service
public class WorkingHoursService {
    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    public WorkingHoursDTO mapToWorkingHoursDTO(WorkingHours workingHours) {
        return new WorkingHoursDTO(
                workingHours.getDay(),
                workingHours.getStartTime(),
                workingHours.getEndTime()
        );
    }

    public List<WorkingHours> updateWorkingHours(Doctor existingDoctor, List<WorkingHours> updatedWorkingHours) {

        existingDoctor.getWorkingHours().clear();

        for (WorkingHours wh : updatedWorkingHours) {
            WorkingHours existingWh = new WorkingHours();
            existingWh.setDay(wh.getDay());
            existingWh.setStartTime(wh.getStartTime());
            existingWh.setEndTime(wh.getEndTime());
            existingWh.setDoctor(existingDoctor);

            existingDoctor.getWorkingHours().add(existingWh);
        }

        return workingHoursRepository.saveAll(existingDoctor.getWorkingHours());
    }


}
