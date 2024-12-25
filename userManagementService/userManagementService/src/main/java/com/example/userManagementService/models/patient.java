package com.example.userManagementService.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "patients")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class patient implements Serializable{
    @Id
    public Long id;
    @Column(name = "medical_history")
    public String medicalHistory;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private users user;
}
