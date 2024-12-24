package com.example.userManagementService.repository;

import com.example.userManagementService.models.doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface doctorRepository extends JpaRepository<doctor, Long> {
}
