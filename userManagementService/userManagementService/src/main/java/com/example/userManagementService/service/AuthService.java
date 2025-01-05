package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.TokenExpiredException;
import com.example.userManagementService.exceptions.UnauthorizedAccessException;
import com.example.userManagementService.dto.LoginRequest;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private static final String SecurityQueue = "securityQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${jwt.accesstoken.expiration}")
    long accessTokenExpiration;
    @Value("${jwt.refreshtoken.expiration}")
    long refreshTokenExpiration;

    public Map<String,String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String accessToken = jwtService.generateToken(authentication.getName(),
                authentication.getAuthorities().iterator().next().getAuthority(),
                accessTokenExpiration);
        String refreshToken = jwtService.generateToken(authentication.getName(),
                null,
                refreshTokenExpiration);

        Map<String,String> tokens = new HashMap<>();
        tokens.put("Access-token",accessToken);
        tokens.put("Refresh-token",refreshToken);

//         Send the JSON message to RabbitMQ
        rabbitTemplate.convertAndSend(SecurityQueue, accessToken);
        return tokens;
    }


    public Map<String,String> refreshToken(HttpServletRequest request, HttpServletResponse response){
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;
        String accessToken = "";
        String newRefreshToken ="";

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new HashMap<>();
        }

        refreshToken = authHeader.substring(7).trim();
        username = jwtService.extractUsername(request);

        System.out.println(refreshToken);
        if (username != null) {
            Optional<Users> user = userRepository.findByUsername(username);
            System.out.println(accessToken);
            System.out.println(newRefreshToken);
            System.out.println(jwtService.isTokenValid(request,user.get().getUsername()));
            if (jwtService.isTokenValid(request, user.get().getUsername())) {
                accessToken = jwtService.generateToken(username,user.get().getRole().getName(),accessTokenExpiration);
                newRefreshToken = jwtService.generateToken(username,null,refreshTokenExpiration);
            }
        }
        Map<String,String> tokens = new HashMap<>();
        tokens.put("Access-token",accessToken);
        tokens.put("Refresh-token",newRefreshToken);
        return tokens;
    }


    public void logout(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        final String token;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        token = authHeader.substring(7).trim();
        jwtService.extractClaims(token).setExpiration(new Date(System.currentTimeMillis()));
    }

    public  void validateUserAccess(Long userIdFromToken, Long requestedUserId) {
        if (userIdFromToken == null) {
            throw new TokenExpiredException("Invalid or missing token.");
        }

        if (!userIdFromToken.equals(requestedUserId)) {
            throw new UnauthorizedAccessException("You do not have access to view this resource.");
        }
    }


}

