package com.example.developerIQ.authservice.controller;

import com.example.developerIQ.authservice.dto.AuthenticationRequest;
import com.example.developerIQ.authservice.entity.UserCredentials;
import com.example.developerIQ.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register-client")
    public ResponseEntity<String> addNewUser(@RequestBody UserCredentials user) {
        return authenticationService.saveUser(user);
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthenticationRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authenticationService.generateToken(authRequest.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid access");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        return authenticationService.validateToken(token);
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Health Check OK");
    }

}
