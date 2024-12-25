package com.example.userManagementService.repository;

import com.example.userManagementService.models.patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface patientRepository extends JpaRepository<patient, Long> {

}

