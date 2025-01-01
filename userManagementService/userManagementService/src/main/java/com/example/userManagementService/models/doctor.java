package com.example.userManagementService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class doctor implements Serializable {
    @Id
    private Long id;
    private String specialty;
    private String experienceYears;

    @OneToOne(cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<workingHours> workingHours;

}
