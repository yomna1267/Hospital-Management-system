package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.IncorrectPasswordException;
import com.example.userManagementService.exceptions.UserNotFoundException;
import com.example.userManagementService.models.ChangePasswordRequest;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;

    public void changePassword(String username,ChangePasswordRequest changePasswordRequest)
    {
        Users connectedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + "is not found"));

        if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),connectedUser.getPassword()))
            throw new IncorrectPasswordException("Password is incorrect for user : "+username);

        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword()))
            throw new IncorrectPasswordException("No Matching between new password and its confirmation");

        connectedUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(connectedUser);
    }

    public Users forgetPassword(String username) {
        Users connectedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + "is not found"));
        String userEmail = connectedUser.getEmail();
        String value = emailService.sendForgetPasswordCode(userEmail);
        redisService.redisSaveForgetPasswordCode(username,value);
        return connectedUser;
    }

    public void resetPassword(String username,String code , String newPassword){
        Optional<Users> user = userRepository.findByUsername(username);
        Jedis jedis = new Jedis("localhost",6379);

        if(passwordEncoder.encode(code).equals(jedis.get(username))
                && jedis.ttl(username)>=0)
        {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user.get());
        }
    }

}