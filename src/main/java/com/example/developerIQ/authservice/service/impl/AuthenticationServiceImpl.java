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
    public String saveUser(UserCredentials credential) {
        try {
            String encrypted = passwordEncoder.encode(credential.getPassword());
            credential.setId(UUID.randomUUID().toString().split("-")[0]);
            credential.setPassword(encrypted);
            repository.save(credential);
            return "user added to the system";
        } catch(DataAccessException | NullPointerException e) {
            logger.error("error saving user credentials");
            return "error saving user details";
        }
    }

    @Override
    public String generateToken(String username) {
        try {
            return jwtService.generateToken(username);
        } catch (JwtException e) {
            logger.error("token cannot be generated", e);
            return "token generation error";
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            return jwtService.validateToken(token);
        } catch (JwtException e) {
            logger.error("token cannot be validated", e);
            return "token validation error";
        }
    }

}
