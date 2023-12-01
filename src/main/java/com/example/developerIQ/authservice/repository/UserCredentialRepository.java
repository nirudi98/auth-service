package com.example.developerIQ.authservice.repository;

import com.example.developerIQ.authservice.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCredentialRepository extends MongoRepository<UserCredentials, String> {
    Optional<UserCredentials> findByName(String username);
}