package com.developerdreamteam.jia.auth.model.dto;

import lombok.Data;


@Data
public class UserDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
