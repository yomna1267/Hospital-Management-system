package com.example.userManagementService.models;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
}
