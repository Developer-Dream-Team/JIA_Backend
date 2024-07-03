package com.developerdreamteam.jia.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
