package com.example.userManagementService.controller;

import com.example.userManagementService.models.ChangePasswordRequest;
import com.example.userManagementService.models.ForgetPasswordRequest;
import com.example.userManagementService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    @PutMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                               Principal connectedUser){
        userService.changePassword(changePasswordRequest,connectedUser);
    }

    @PostMapping("/forget-password/{username}")
    public ResponseEntity<Void> forgetPassword(@PathVariable String username){
        userService.forgetPassword(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String username,
                                                @RequestParam String code,
                                                @RequestParam String newPassword){
        userService.resetPassword(username,code,newPassword);
        return ResponseEntity.ok().build();
    }
}
