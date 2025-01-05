package com.example.workflow_service.scheduling;

import com.example.workflow_service.enums.Patient_States;
import com.example.workflow_service.repository.PatientStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class PatientStatusScheduler {

    @Autowired
    private PatientStatusRepository patientStatusRepository;

    //sec min hours days etc
    @Scheduled(cron = "0 0 0 * * ?") // Run the job daily at midnight
    //@Scheduled(fixedRate = 20000) // Runs every 20 seconds
    public void cancelLongRegisteredPatients() {
        // Fetch all patients
        var patients = patientStatusRepository.findAll();

        patients.stream()
                .filter(patient ->
                        (patient.getState() == Patient_States.REGISTERED || patient.getState() == Patient_States.UNDERTREATMENT) &&
                                ChronoUnit.MONTHS.between(patient.getUpdatedAt(), LocalDateTime.now()) >= 1)
                .forEach(patient -> {
                    patient.setState(Patient_States.CANCELLED);
                    patientStatusRepository.save(patient); // Update the patient's state
                });
    }
}
