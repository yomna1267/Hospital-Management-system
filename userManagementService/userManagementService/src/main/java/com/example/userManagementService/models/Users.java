package com.example.userManagementService.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;


@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    @Pattern(
            regexp = "^[23]\\d{13}$",
            message = "Invalid National ID format. It must start with 2 or 3 and contain exactly 14 digits."
    )
    private String username;
    private String password;
    @Column(unique = true)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;
    @Column(unique = true)
    @Pattern(
            regexp = "^01[0-2,5]{1}\\d{8}$",
            message = "Invalid phone number format. It must be an 11-digit valid Egyptian number starting with 010, 011, 012, or 015."
    )
    private String phone;
    private String address;
    private String gender;
    private int age;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


}