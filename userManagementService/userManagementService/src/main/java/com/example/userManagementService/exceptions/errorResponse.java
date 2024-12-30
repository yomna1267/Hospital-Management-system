package com.example.userManagementService.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class errorResponse {
    private final String error;
    private final String message;
    private final Integer status;
    private final LocalDateTime timestamp;

    public errorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
