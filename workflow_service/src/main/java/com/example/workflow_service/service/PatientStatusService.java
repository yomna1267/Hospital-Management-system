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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public PatientStatusDTO getStateForOnePatientWithOneAppointment(Long patientId, Long appointmentId)
    {
        PatientStatus patientStatus = patientStatusRepository.findLatestByPatientIdAndAppointmentId(patientId,appointmentId)
                .orElseThrow(()->new EntityNotFoundException("Patient not found"));

        return modelMapper.patientStatusEntityToPatientStatusDto(patientStatus);

    }

    public List<PatientStatusDTO> getStatesForOnePatient(Long patientId){
        List<PatientStatus> patientStatuses = patientStatusRepository.findAllByPatientIdOrderByCreatedAtAsc(patientId);
        if (patientStatuses.isEmpty()) {
            throw new EntityNotFoundException("No patient status records found for this patient and appointment");
        }
        return patientStatuses.stream()
                .map(modelMapper::patientStatusEntityToPatientStatusDto)
                .collect(Collectors.toList());
    }

    @RabbitListener(queues = "statusQueue")
    public void listen(String message)
    {
        try {
            // Deserialize the incoming JSON message
            PatientStatusMessage patientStatusMessage = objectMapper.readValue(message, PatientStatusMessage.class);

            Optional<PatientStatus> patientStatusOptional = patientStatusRepository.findLatestByPatientIdAndAppointmentId(patientStatusMessage.getPatientId(),patientStatusMessage.getAppointmentId());
            if(!patientStatusOptional.isPresent())
            {
                if(patientStatusMessage.getEvent()==Patient_Events.Register)
                {
                    PatientStatus patientStatus1 = new PatientStatus(patientStatusMessage.getPatientId(),patientStatusMessage.getAppointmentId(), Patient_States.REGISTERED);
                    patientStatusRepository.save(patientStatus1);
                }else {
                    throw new EntityNotFoundException("NO Patient found");
                }
            }

            else {
                PatientStatus patientStatus = patientStatusOptional.get();
                System.out.println(patientStatus.getId()) ;
                System.out.println(patientStatus.getState());

                if(patientStatus.getState()==Patient_States.DISCHARGED || patientStatus.getState()==Patient_States.CANCELLED)
                {
                    throw new IllegalStateException("Patient has already finished this appointment. State cannot be changed.");
                }
                StateMachine<Patient_States, Patient_Events> stateMachine = stateMachineFactory.getStateMachine();
                stateMachine.start();
                stateMachine.getStateMachineAccessor()
                        .doWithAllRegions(accessor -> accessor.resetStateMachine(
                                new DefaultStateMachineContext<>(patientStatus.getState(), null, null, null)));
                stateMachine.getExtendedState().getVariables().put("patientStatus", patientStatus);

                Patient_Events currentEvent = patientStatusMessage.getEvent();

                stateMachine.sendEvent(currentEvent); //TREATMENT_STARTED, TREATMENT_COMPLETED, SKIP_TREATMENT, APPOINTMENT_CANCELLED

                patientStatus.setState(stateMachine.getState().getId());
                patientStatusRepository.save(patientStatus);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
