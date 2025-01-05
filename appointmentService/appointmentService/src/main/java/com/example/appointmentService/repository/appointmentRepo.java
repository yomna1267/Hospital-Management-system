package com.example.appointmentService.repository;

import com.example.appointmentService.models.appointment;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface appointmentRepo extends JpaRepository<appointment, Long> {
    List<appointment> findByPatientId(Long patientId);

    @Query("SELECT a FROM appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :appointmentDate AND a.status <> 'CANCELLED'")
    Optional<appointment> findByDoctorIdAndAppointmentDate(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDateTime appointmentDate);

    List<appointment> findByDoctorId(Long doctorId);

    @Query("SELECT a FROM appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.status <> 'CANCELLED'")
    Optional<appointment> findByDoctorIdAndTimeRangeCreate(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.status <> 'CANCELLED' AND a.patientId <> :patientId")
    Optional<appointment> findByDoctorIdAndTimeRange(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("patientId") Long patientId);

    Optional<appointment> findByIdAndDoctorId(Long appointmentId, Long doctorId);

    Optional<appointment> findByIdAndPatientId(Long appointmentId, Long patientId);
}

