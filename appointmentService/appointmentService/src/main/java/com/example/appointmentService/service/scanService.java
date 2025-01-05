package com.example.appointmentService.service;

import com.example.appointmentService.exceptions.AppointmentNotFoundException;
import com.example.appointmentService.models.appointment;
import com.example.appointmentService.models.scanResult;
import com.example.appointmentService.repository.appointmentRepo;
import com.example.appointmentService.repository.scanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class scanService {

    @Autowired
    private scanRepo scanRepository;

    @Autowired
    private appointmentRepo appointmentRepository;

    public scanResult addScanResult(Long doctorId, Long patientId, scanResult scanResult) {
        appointment appointment = appointmentRepository.findById(scanResult.getAppointment().getId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId) || !appointment.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot add scan result for another doctor's patient");        }

        scanResult.setDoctorId(doctorId);
        scanResult.setPatientId(patientId);
        scanResult.setAppointment(appointment);

        return scanRepository.save(scanResult);
    }

    public List<scanResult> getScanResultsByPatientId(Long patientId) {
        List<scanResult> results = scanRepository.findByPatientId(patientId);

        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Scan results found");
        }
        return results;
    }

    public scanResult getScanResultById(Long patientId, Long scanId) {
        scanResult result = scanRepository.findById(scanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scan result not found"));

        if (!result.getPatientId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view another patient's scan result");
        }

        return result;
    }
}
