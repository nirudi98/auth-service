package com.example.developerIQ.authservice.service.impl;

import com.example.developerIQ.authservice.config.AuthenticationConfig;
import com.example.developerIQ.authservice.config.TestConfig;
import com.example.developerIQ.authservice.entity.UserCredentials;
import com.example.developerIQ.authservice.repository.UserCredentialRepository;
import com.example.developerIQ.authservice.service.AuthenticationService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import({AuthenticationConfig.class})
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserCredentialRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private Key mockKey;

    @Test
    void saveUser() {
        UserCredentials mock = generateUserCredentials();
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserCredentials.class))).thenReturn(mock);

        String result = authenticationService.saveUser(mock);
        assertEquals("user added to the system", result);
    }

    @Test
    void testSaveUser_Error() {
        UserCredentials userCredentials = generateUserCredentials();
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserCredentials.class))).thenThrow(new DataAccessException("Simulated error") {});
        String result = authenticationService.saveUser(userCredentials);

        assertEquals("error saving user details", result);
    }

    @Test
    void generateToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertNotNull(token);
    }

    @Test
    void testGenerateToken_Exception() {
        JwtServiceImpl jwtTokenUtil = new JwtServiceImpl() {
            @Override
            public Key getSignKey() {
                return Keys.hmacShaKeyFor("invalidKey".getBytes());
            }
        };
        String username = "testUser";

        assertThatThrownBy(() -> jwtTokenUtil.generateToken(username))
                .isInstanceOf(Exception.class); // Adjust the exception type as needed
    }

    @Test
    void validateToken() {
        JwtServiceImpl tokenValidator = new JwtServiceImpl();
        String validToken = createValidToken();
        String result = tokenValidator.validateToken(validToken);

        assertThat(result).isEqualTo("Successfully validated token");
    }

    @Test
    public void testValidateToken_SignatureException() {
        // Arrange
        JwtServiceImpl tokenValidator = new JwtServiceImpl();
        String invalidToken = createInvalidToken();

        // Act and Assert
        assertThatThrownBy(() -> tokenValidator.validateToken(invalidToken))
                .isInstanceOf(MalformedJwtException.class)
                .hasMessageContaining("JWT strings must contain exactly 2 period characters. Found: 0");
    }


    private UserCredentials generateUserCredentials() {
        UserCredentials mockUserCredentials = new UserCredentials();
        mockUserCredentials.setName("Test");
        mockUserCredentials.setPassword("1234");
        mockUserCredentials.setEmail("test@aaa.com");
        return mockUserCredentials;
    }

    private String createValidToken() {
        Map<String, Object> claims = new HashMap<>();
        String username = "abc";
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(jwtService.getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private String createInvalidToken() {
        return "invalidToken";
    }

}