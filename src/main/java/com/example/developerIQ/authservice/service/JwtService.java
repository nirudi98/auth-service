package com.example.developerIQ.authservice.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    public String generateToken(String username);
    public String validateToken(String token);
}
