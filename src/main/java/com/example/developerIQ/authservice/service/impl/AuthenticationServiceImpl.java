package com.example.developerIQ.authservice.service.impl;

import com.example.developerIQ.authservice.entity.UserCredentials;
import com.example.developerIQ.authservice.repository.UserCredentialRepository;
import com.example.developerIQ.authservice.service.AuthenticationService;
import com.example.developerIQ.authservice.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    @Autowired
    private UserCredentialRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @Override
    public ResponseEntity<String> saveUser(UserCredentials credential) {
        try {
            if(validateRequestBody(credential)) {
                throw new NullPointerException();
            }
            String encrypted = passwordEncoder.encode(credential.getPassword());
            credential.setId(UUID.randomUUID().toString().split("-")[0]);
            credential.setPassword(encrypted);
            repository.save(credential);
            return ResponseEntity.ok("User added to the system");
        } catch(DataAccessException | NullPointerException e) {
            logger.error("error saving user credentials");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving user details");
        }
    }

    @Override
    public ResponseEntity<String> generateToken(String username) {
        try {
            return ResponseEntity.ok(jwtService.generateToken(username));
        } catch (JwtException e) {
            logger.error("token cannot be generated", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("token generation error");
        }
    }

    @Override
    public ResponseEntity<String> validateToken(String token) {
        try {
            return ResponseEntity.ok(jwtService.validateToken(token));
        } catch (JwtException e) {
            logger.error("token cannot be validated", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("token validation error");
        }
    }

    private Boolean validateRequestBody(UserCredentials credentials) {
        return credentials.getName() == null || credentials.getPassword() == null
                || credentials.getName().isEmpty() || credentials.getPassword().isEmpty();
    }

}
