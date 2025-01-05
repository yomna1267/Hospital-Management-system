package com.example.appointmentService.repository;


import com.example.appointmentService.models.scanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface scanRepo extends JpaRepository<scanResult, Long> {
    List<scanResult> findByPatientId(Long patientId);

}

