package com.example.userManagementService.models;

import lombok.Data;

@Data
public class ForgetPasswordRequest {

    private String newPassword;
    private String confirmNewPassword;
}
