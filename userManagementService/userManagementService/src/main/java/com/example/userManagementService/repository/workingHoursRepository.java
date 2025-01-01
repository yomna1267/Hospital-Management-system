package com.example.userManagementService.repository;

import com.example.userManagementService.models.workingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface workingHoursRepository extends JpaRepository<workingHours, Long> {
    List<workingHours> findByDoctorId(Long id);
}
