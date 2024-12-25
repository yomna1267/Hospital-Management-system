package com.example.userManagementService.repository;

import com.example.userManagementService.models.role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface roleRepository extends JpaRepository<role, Long> {
}
