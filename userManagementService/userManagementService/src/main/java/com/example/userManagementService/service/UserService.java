package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.IncorrectPasswordException;
import com.example.userManagementService.exceptions.RateLimitExceedException;
import com.example.userManagementService.exceptions.UserNotFoundException;
import com.example.userManagementService.dto.ChangePasswordRequest;
import com.example.userManagementService.dto.ResetPasswordRequest;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpUtil otpUtil;
    private final RedisService redisService;
    private final JWTService jwtService;
    private final RateLimiterService rateLimiterService;


    public void changePassword(HttpServletRequest request, ChangePasswordRequest changePasswordRequest) {
        String connectedUserUsername = jwtService.extractUsername(request);
        Users connectedUser = userRepository.findByUsername(connectedUserUsername)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + connectedUserUsername + " is not found"));

        if (rateLimiterService.isRateLimited(connectedUserUsername)) {
            throw new RateLimitExceedException("Too many password change attempts. Please try again later.");
        }

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), connectedUser.getPassword())) {
            throw new IncorrectPasswordException("Password is incorrect for user: " + connectedUserUsername);
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IncorrectPasswordException("No match between new password and its confirmation.");
        }

        connectedUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(connectedUser);
    }

    public Users forgetPassword(String username) {
        Users connectedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + "is not found"));
        String userEmail = connectedUser.getEmail();
        String value = emailService.sendForgetPasswordCode(userEmail,otpUtil.generateOtp());
        redisService.redisSaveForgetPasswordCode(username,value);
        return connectedUser;
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        Users connectedUser = userRepository.findByUsername(resetPasswordRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username: " + resetPasswordRequest.getUsername() + " is not found"));


            redisService.validateOtp(resetPasswordRequest.getUsername(),resetPasswordRequest.getCode());
            connectedUser.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            userRepository.save(connectedUser);
    }
}