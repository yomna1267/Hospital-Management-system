package com.example.userManagementService.repository;

import com.example.userManagementService.models.doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface doctorRepository extends JpaRepository<doctor, Long>, JpaSpecificationExecutor<doctor> {
    List<doctor> findByspecialty(String specialty);
}
