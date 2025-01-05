package com.example.userManagementService.service;

import com.example.userManagementService.models.ChangePasswordRequest;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;

    public void changePassword(ChangePasswordRequest changePasswordRequest
                               ,Principal principal)
    {
        String username = principal.getName();
        Users connectedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println(connectedUser.getPassword());
        System.out.println(passwordEncoder.encode(changePasswordRequest.getCurrentPassword()));
        System.out.println(changePasswordRequest.getConfirmPassword());
        System.out.println(changePasswordRequest.getCurrentPassword());

        if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),connectedUser.getPassword()))
            throw new IllegalStateException("wrong password");

        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword()))
<<<<<<< HEAD
            throw new IncorrectPasswordException("No Matching between new password and its confirmation");
=======
                 throw new IllegalStateException("No Matching");
>>>>>>> parent of b1a71f2 (some enhancements & seka Redis)

        connectedUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(connectedUser);
    }

    public Optional<Users> forgetPassword(String username) {
       Optional<Users> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }
       String userEmail = user.get().getEmail();
        emailService.sendForgetPasswordCode(userEmail);
        return user;
    }

    public void resetPassword(String username,String code , String newPassword){
        redisService.redisSaveForgetPasswordCode(username);
        Optional<Users> user = forgetPassword(username);
        Jedis jedis = new Jedis("localhost",6379);
        System.out.println(jedis.get(username));
        if(code.equals(jedis.get(username)) && jedis.ttl(username)>=0)
            user.get().setPassword(passwordEncoder.encode(newPassword));
    }

}