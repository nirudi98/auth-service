package com.example.developerIQ.authservice.integration;

import com.example.developerIQ.authservice.dto.AuthenticationRequest;
import com.example.developerIQ.authservice.entity.UserCredentials;
import com.example.developerIQ.authservice.repository.UserCredentialRepository;
import com.example.developerIQ.authservice.service.impl.JwtServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerTests {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private JwtServiceImpl jwtService;

    @Test
    void testAddNewUser() {
        UserCredentials user = new UserCredentials();
        user.setName("test user");
        user.setPassword("1234");
        user.setEmail("test_mail");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/register-client",
                user,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added to the system", response.getBody());
    }

    @Test
    void testAddNewUser_exception() {
        UserCredentials user = new UserCredentials();
        user.setName("test user");
        user.setPassword(null);
        user.setEmail("test_mail");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/register-client",
                user,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error saving user details", response.getBody());
    }

    @Test
    void testGetToken() {
        //register client first
        UserCredentials user = new UserCredentials();
        user.setName("test_nuri");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("test_mail");
        user.setId(UUID.randomUUID().toString().split("-")[0]);
        userCredentialRepository.save(user);

        AuthenticationRequest authRequest = new AuthenticationRequest("test_nuri", "1234");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/token",
                authRequest,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetToken_exception() {
        AuthenticationRequest authRequest = new AuthenticationRequest("test", "test");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/token",
                authRequest,
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testValidateToken() {
        //register client first
        UserCredentials user = new UserCredentials();
        user.setName("test_sula");
        user.setPassword(passwordEncoder.encode("sula12"));
        user.setEmail("test_mail");
        user.setId(UUID.randomUUID().toString().split("-")[0]);
        userCredentialRepository.save(user);

        //retrieve token
        Map<String, Object> mockClaims = new HashMap<>();
        String token = jwtService.createToken(mockClaims, "test_sula");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/auth/validate")
                .queryParam("token", token);

        ResponseEntity<String> response = restTemplate.getForEntity(
                builder.toUriString(),
                String.class
        );
        assertEquals("Successfully validated token", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testValidateToken_exception() {
        AuthenticationRequest authRequest = new AuthenticationRequest("test", "test");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/token",
                authRequest,
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }



}
