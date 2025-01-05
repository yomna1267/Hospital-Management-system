package com.example.appointmentService.service;


import com.example.appointmentService.exceptions.AppointmentNotFoundException;
import com.example.appointmentService.models.appointment;
import com.example.appointmentService.models.prescription;
import com.example.appointmentService.repository.appointmentRepo;
import com.example.appointmentService.repository.prescriptionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class prescriptionService {

    @Autowired
    private prescriptionRepo prescriptionRepository;

    @Autowired
    private appointmentRepo appointmentRepository;

    public prescription addPrescription(Long doctorId, Long patientId, prescription prescription) {
        appointment appointment = appointmentRepository.findById(prescription.getAppointment().getId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId) || !appointment.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot add a prescription for another doctor's patient");
        }

        prescription.setDoctorId(doctorId);
        prescription.setPatientId(patientId);
        prescription.setAppointment(appointment);

        return prescriptionRepository.save(prescription);
    }

    public List<prescription> getPrescriptionsByPatientId(Long patientId) {
        List<prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

        if (prescriptions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No prescriptions found");
        }

        return prescriptions;
    }

    public prescription getPrescriptionById(Long patientId, Long prescriptionId) {
        prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found"));

        if (!prescription.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view another patient's prescription");
        }

        return prescription;
    }
}

