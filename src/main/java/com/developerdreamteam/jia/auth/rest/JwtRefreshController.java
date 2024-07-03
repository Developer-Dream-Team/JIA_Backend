package com.developerdreamteam.jia.auth.rest;

import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.model.dto.LoginResponseDTO;
import com.developerdreamteam.jia.auth.security.jwt.JwtTokenUtil;
import com.developerdreamteam.jia.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class JwtRefreshController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/token/refresh")
    public ResponseEntity<ServiceResponse<LoginResponseDTO>> refreshToken(@RequestParam String refreshToken) {
        try {
            if (jwtTokenUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServiceResponse<>(HttpStatus.UNAUTHORIZED, "Refresh token expired", null));
            }

            String username = jwtTokenUtil.extractUsername(refreshToken);
            UserDetails userDetails = authService.loadUserByUsername(username);

            if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServiceResponse<>(HttpStatus.UNAUTHORIZED, "Invalid refresh token", null));
            }

            String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);
            LoginResponseDTO response = new LoginResponseDTO(newAccessToken, refreshToken, username);

            return ResponseEntity.ok(new ServiceResponse<>(HttpStatus.OK, "Token refreshed successfully", response));
        } catch (Exception e) {
            ServiceResponse<LoginResponseDTO> errorResponse = new ServiceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
