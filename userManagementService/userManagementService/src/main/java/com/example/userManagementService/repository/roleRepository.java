package com.example.userManagementService.repository;

import com.example.userManagementService.models.role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface roleRepository extends JpaRepository<role, Long> {
    Optional<Object> findByname(String doctor);
}
