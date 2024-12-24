package com.example.userManagementService.repository;

import com.example.userManagementService.models.users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository <users, Long> {
}
