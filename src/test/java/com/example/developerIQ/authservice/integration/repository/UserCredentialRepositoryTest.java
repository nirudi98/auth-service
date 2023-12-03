package com.example.developerIQ.authservice.integration.repository;

import com.example.developerIQ.authservice.entity.UserCredentials;
import com.example.developerIQ.authservice.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserCredentialRepositoryTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

    @Autowired
    UserCredentialRepository userCredentialRepository;

    @BeforeEach
    void setUp() {
        List<UserCredentials> credentialsList = List.of(new UserCredentials
                (null,"test_nuri", "1234","testmail@gmail.com"));
        userCredentialRepository.saveAll(credentialsList);
    }

    @Test
    void connectionEstablished() {
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnUserByUsername() {
        UserCredentials credentials = userCredentialRepository.findByName("test_nuri").orElseThrow();
        assertEquals("testmail@gmail.com", credentials.getEmail(), "User email" +
                "should be 'testmail@gmail.com'");
    }

}
