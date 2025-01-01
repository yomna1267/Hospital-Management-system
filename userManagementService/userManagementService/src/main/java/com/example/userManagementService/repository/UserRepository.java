package com.example.userManagementService.repository;

import com.example.userManagementService.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <Users, Long> {
    List<Users> findByRoleName(String roleName);
    Optional<Users> findByUsername(String userName);
    boolean existsByEmail(String email);
}
