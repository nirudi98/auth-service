package com.example.developerIQ.authservice.service;


import com.example.developerIQ.authservice.entity.UserCredentials;

public interface AuthenticationService {

    public String saveUser(UserCredentials credential);

    public String generateToken(String username);

    public String validateToken(String token);


//    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
