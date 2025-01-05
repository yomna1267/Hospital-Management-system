package com.example.userManagementService.controller;

import com.example.userManagementService.models.ChangePasswordRequest;
import com.example.userManagementService.models.ResetPasswordRequest;
import com.example.userManagementService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            @RequestBody ChangePasswordRequest changePasswordRequest)
    {
        userService.changePassword(request, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/forget-password/{username}")
    public ResponseEntity<String> forgetPassword(@PathVariable String username)
    {
        userService.forgetPassword(username);
        return ResponseEntity.ok("Code is sent to ur email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request){

            userService.resetPassword(request);
            return ResponseEntity.noContent().build();

    }


}
