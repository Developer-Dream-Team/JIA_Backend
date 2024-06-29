package com.developerdreamteam.jia.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String timestamp;
}
