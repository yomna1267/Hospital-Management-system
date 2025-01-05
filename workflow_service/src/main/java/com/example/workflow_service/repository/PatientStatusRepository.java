package com.example.workflow_service.repository;

import com.example.workflow_service.entities.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientStatusRepository extends JpaRepository<PatientStatus,Long> {

    @Query(value = "SELECT * FROM patient_status ps WHERE ps.patient_id = :patientId AND ps.appointment_id = :appointmentId ORDER BY ps.id DESC LIMIT 1", nativeQuery = true)
    Optional<PatientStatus> findLatestByPatientIdAndAppointmentId(@Param("patientId") Long patientId,
                                                                  @Param("appointmentId") Long appointmentId);

    // Custom query to get all patient states for a given patient_id
    List<PatientStatus> findAllByPatientIdOrderByCreatedAtAsc(Long patientId);
}
