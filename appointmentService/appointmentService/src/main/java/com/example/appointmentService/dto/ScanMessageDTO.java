package com.example.appointmentService.dto;

<<<<<<<< HEAD:appointmentService/appointmentService/src/main/java/com/example/appointmentService/dto/ScanMessageDTO.java
import com.example.userManagementService.enums.PatientStatus;
========
>>>>>>>> 0903e0ddec8e7446518ca5a8aeff797fbe5a83c6:appointmentService/appointmentService/src/main/java/com/example/appointmentService/dto/statusMessageDTO.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
<<<<<<<< HEAD:appointmentService/appointmentService/src/main/java/com/example/appointmentService/dto/ScanMessageDTO.java
public class ScanMessageDTO {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private PatientStatus status;
========
@NoArgsConstructor
public class statusMessageDTO {
    Long patientId;
    Long doctorId;
    Long appointmentId;
    private State status;
>>>>>>>> 0903e0ddec8e7446518ca5a8aeff797fbe5a83c6:appointmentService/appointmentService/src/main/java/com/example/appointmentService/dto/statusMessageDTO.java
}
