package com.example.userManagementService.dto;

import lombok.Data;

@Data
public class ForgetPasswordRequest {

    private String newPassword;
    private String confirmNewPassword;
}
