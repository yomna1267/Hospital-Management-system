package com.example.workflow_service.service;

import com.example.workflow_service.dto.PatientStatusDTO;
import com.example.workflow_service.dto.PatientStatusMessage;
import com.example.workflow_service.entities.PatientStatus;
import com.example.workflow_service.enums.Patient_Events;
import com.example.workflow_service.enums.Patient_States;
import com.example.workflow_service.mapper.PatientStatusMapper;
import com.example.workflow_service.repository.PatientStatusRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PatientStatusService {

    private PatientStatusRepository patientStatusRepository;

    private StateMachineFactory<Patient_States, Patient_Events> stateMachineFactory;

    private PatientStatusMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    public PatientStatusService(PatientStatusRepository patientStatusRepository, StateMachineFactory<Patient_States, Patient_Events> stateMachineFactory, PatientStatusMapper modelMapper) {
        this.patientStatusRepository = patientStatusRepository;
        this.stateMachineFactory = stateMachineFactory;
        this.modelMapper = modelMapper;
    }

    public PatientStatusDTO createRegisteredEvent(Long patientId, Long appointmentId)
    {
        PatientStatus patientStatus = new PatientStatus(patientId,appointmentId, Patient_States.REGISTERED);


        patientStatusRepository.save(patientStatus);
        return modelMapper.patientStatusEntityToPatientStatusDto(patientStatus);
    }

    public PatientStatusDTO handleEvents(Long patientId, Long appointmentId, Patient_Events event)
    {
        PatientStatus patientStatus = patientStatusRepository.findLatestByPatientIdAndAppointmentId(patientId,appointmentId)
                .orElseThrow(()->new EntityNotFoundException("Patient not found"));

        System.out.println(patientStatus.getId()) ;
        System.out.println(patientStatus.getState());

        StateMachine<Patient_States, Patient_Events> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(accessor -> accessor.resetStateMachine(
                        new DefaultStateMachineContext<>(patientStatus.getState(), null, null, null)));
        stateMachine.getExtendedState().getVariables().put("patientStatus", patientStatus);
        stateMachine.sendEvent(event);

        patientStatus.setState(stateMachine.getState().getId());
        patientStatusRepository.save(patientStatus);
        return modelMapper.patientStatusEntityToPatientStatusDto(patientStatus);

    }

    //@RabbitListener(queues = "statusQueue")
    public void listen(String message)
    {
        try {
            // Deserialize the incoming JSON message
            PatientStatusMessage patientStatusMessage = objectMapper.readValue(message, PatientStatusMessage.class);

            //once an appointment is created
            if(patientStatusMessage.getEvent()==Patient_Events.Register)
            {
                PatientStatus patientStatus = new PatientStatus(patientStatusMessage.getPatientId(),patientStatusMessage.getAppointmentId(), Patient_States.REGISTERED);
                patientStatusRepository.save(patientStatus);
                return;
            }

            //post scans, discharging, cancel
            PatientStatus patientStatus = patientStatusRepository.findLatestByPatientIdAndAppointmentId(patientStatusMessage.getPatientId(),patientStatusMessage.getAppointmentId())
                    .orElseThrow(()->new EntityNotFoundException("Patient not found"));

            System.out.println(patientStatus.getId()) ;
            System.out.println(patientStatus.getState());


            StateMachine<Patient_States, Patient_Events> stateMachine = stateMachineFactory.getStateMachine();
            stateMachine.start();
            stateMachine.getStateMachineAccessor()
                    .doWithAllRegions(accessor -> accessor.resetStateMachine(
                            new DefaultStateMachineContext<>(patientStatus.getState(), null, null, null)));
            stateMachine.getExtendedState().getVariables().put("patientStatus", patientStatus);

            stateMachine.sendEvent(patientStatusMessage.getEvent()); //TREATMENT_STARTED, TREATMENT_COMPLETED, SKIP_TREATMENT, APPOINTMENT_CANCELLED

            patientStatus.setState(stateMachine.getState().getId());
            patientStatusRepository.save(patientStatus);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
