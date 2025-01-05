package com.example.workflow_service.mapper;

import com.example.workflow_service.dto.PatientStatusDTO;
import com.example.workflow_service.entities.PatientStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientStatusMapper {
    @Autowired
    private ModelMapper modelMapper;

    public PatientStatus patientStatusDtoToPatientStatusEntity(PatientStatusDTO userDto)
    {
        return modelMapper.map(userDto, PatientStatus.class);
    }
    public PatientStatusDTO patientStatusEntityToPatientStatusDto(PatientStatus userEntity)
    {
        return modelMapper.map(userEntity, PatientStatusDTO.class);
    }

}