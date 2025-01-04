package com.example.userManagementService.service;

import com.example.userManagementService.models.LoginRequest;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
        username = jwtService.extractUsername(refreshToken);

        System.out.println(refreshToken);
        if (username != null) {
            Optional<Users> user = userRepository.findByUsername(username);
            System.out.println(accessToken);
            System.out.println(newRefreshToken);
            System.out.println(jwtService.isTokenValid(refreshToken,user.get().getUsername()));
            if (jwtService.isTokenValid(refreshToken, user.get().getUsername())) {
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

}

