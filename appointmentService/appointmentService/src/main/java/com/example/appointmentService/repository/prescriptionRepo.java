package com.example.appointmentService.repository;


import com.example.appointmentService.models.prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface prescriptionRepo extends JpaRepository<prescription, Long> {
    List<prescription> findByPatientId(Long patientId);
}
