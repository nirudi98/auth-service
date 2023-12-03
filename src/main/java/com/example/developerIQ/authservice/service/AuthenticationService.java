package com.example.developerIQ.authservice.service;


import com.example.developerIQ.authservice.entity.UserCredentials;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    public ResponseEntity<String> saveUser(UserCredentials credential);

    public ResponseEntity<String> generateToken(String username);

    public ResponseEntity<String> validateToken(String token);


//    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
