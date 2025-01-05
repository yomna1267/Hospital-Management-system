package com.example.userManagementService.controller;

import com.example.userManagementService.dto.LoginRequest;
import com.example.userManagementService.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginRequest loginRequest)
    {
        return authService.login(loginRequest);
    }


    @PostMapping("/refresh-token")
    public Map<String,String> refreshToken(HttpServletRequest request,
                                           HttpServletResponse response)
    {
        return  authService.refreshToken(request,response);
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request)
    {
        authService.logout(request);
    }


}
