package com.developerdreamteam.jia.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTTokenDTO {
    private String accessToken;
    private String refreshToken;
}
