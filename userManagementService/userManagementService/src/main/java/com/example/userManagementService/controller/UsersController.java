package com.example.userManagementService.controller;

import com.example.userManagementService.models.ChangePasswordRequest;
import com.example.userManagementService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PutMapping("/change-password/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username,
                               @RequestBody ChangePasswordRequest changePasswordRequest)
    {
        userService.changePassword(username, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/forget-password/{username}")
    public ResponseEntity<?> forgetPassword(@PathVariable String username)
    {
        userService.forgetPassword(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String username,
                                                @RequestParam String code,
                                                @RequestParam String newPassword){

            userService.resetPassword(username,code,newPassword);
            return ResponseEntity.noContent().build();

    }


}
