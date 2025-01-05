package com.example.appointmentService.controller;

import com.example.appointmentService.models.scanResult;
import com.example.appointmentService.service.scanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class scanController {

    @Autowired
    private scanService scanService;

    @PostMapping("/doctors/{doctorId}/patients/{patientId}/scan-results")
    public ResponseEntity<scanResult> addScanResult(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            @RequestBody scanResult scanResult) {

        scanResult createdScanResult = scanService.addScanResult(doctorId, patientId, scanResult);
        return ResponseEntity.ok(createdScanResult);
    }
    @GetMapping("/patients/{patientId}/scan-results")
    public ResponseEntity<List<scanResult>> getScanResultsForPatient(@PathVariable Long patientId) {
        List<scanResult> scanResults = scanService.getScanResultsByPatientId(patientId);
        return ResponseEntity.ok(scanResults);
    }

    @GetMapping("/patients/{patientId}/scan-results/{scanId}")
    public ResponseEntity<scanResult> getScanResultById(@PathVariable Long patientId, @PathVariable Long scanId) {
        scanResult scanResult = scanService.getScanResultById(patientId, scanId);
        return ResponseEntity.ok(scanResult);
    }
}
