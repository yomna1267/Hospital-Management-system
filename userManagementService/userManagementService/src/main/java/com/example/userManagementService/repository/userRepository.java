package com.example.userManagementService.repository;

import com.example.userManagementService.models.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface userRepository extends JpaRepository <users, Long> {
    List<users> findByRoleName(String roleName);
    Optional<users> findByUsername(String userName);
    boolean existsByEmail(String email);

}
