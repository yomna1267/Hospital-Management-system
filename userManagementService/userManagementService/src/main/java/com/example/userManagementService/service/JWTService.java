package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.OtpOrTokenExpiredException;
import com.example.userManagementService.exceptions.TokenExpiredException;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class JWTService {

    private final Key key;
    private final UserRepository userRepository;

    public JWTService(@Value("${jwt.secret.key}") String secretKey, UserRepository userRepository) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.userRepository = userRepository;
    }


    public String generateToken(String username, String role , long expiration) {
        Optional<Users> user = userRepository.findByUsername(username);
        return Jwts.builder()
                .setSubject(username)
                .claim("id",user.get().getId())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        return extractClaims(token).getSubject();
    }
    public String extractTokenFromHeader(HttpServletRequest request){
        return request.getHeader("Authorization").substring(7);
    }
    public long extractID(HttpServletRequest request) {
        return extractClaims(extractTokenFromHeader(request)).get("id",Long.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean isTokenValid(HttpServletRequest request, String username) {
        try {
            String token = extractTokenFromHeader(request);
            if (token == null) {
                throw new IllegalArgumentException("No token in the request header.");
            }
            String tokenUsername = extractUsername(request);
            return username.equals(tokenUsername) && !isTokenExpired(token);

        }
        catch (OtpOrTokenExpiredException exception){
           throw new OtpOrTokenExpiredException("Token is expired");
        }
    }

    private boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        Date expirationDate = claims.getExpiration();
        if (expirationDate.before(new Date())) {
            throw new TokenExpiredException("The token has expired.");
        }

        return false;
        //return extractClaims(token).getExpiration().before(new Date());
    }
}
