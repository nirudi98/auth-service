package com.example.developerIQ.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_credentials")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials {

    @Id
    private String id;
    private String name;
    private String password;
    private String email;
}
