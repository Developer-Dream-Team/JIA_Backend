package com.developerdreamteam.jia.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String _id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean active;
    private String timestamp;
    private String activationCode;
    private String role;
    private LocalDateTime activationExpiry;
}
