package com.developerdreamteam.jia.auth.rest;

import com.developerdreamteam.jia.auth.exceptions.UserAlreadyActivatedException;
import com.developerdreamteam.jia.auth.exceptions.UserNotFoundException;
import com.developerdreamteam.jia.auth.model.dto.*;
import com.developerdreamteam.jia.auth.response.ApiResponse;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.security.custom.CustomUserDetails;
import com.developerdreamteam.jia.auth.security.jwt.JwtTokenUtil;
import com.developerdreamteam.jia.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResponse<UserResponseDTO>> signup(@RequestBody UserDTO userDTO) {
        ServiceResponse<UserResponseDTO> response = authService.saveUser(userDTO);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authService.loadUserByUsername(loginDTO.getUsername());
            String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            );

            JWTTokenDTO jwtTokenDTO = new JWTTokenDTO(accessToken, refreshToken);

            AuthResponseDTO authResponse = new AuthResponseDTO(jwtTokenDTO, userDetailsDTO);
            ServiceResponse<AuthResponseDTO> response = new ServiceResponse<>(HttpStatus.OK, "Login successful", authResponse);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            ServiceResponse<AuthResponseDTO> errorResponse = new ServiceResponse<>(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            ServiceResponse<AuthResponseDTO> errorResponse = new ServiceResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/signup/confirmation")
    public ResponseEntity<ApiResponse> confirmSignup(@RequestParam("success") String activationCode) {
        try {
            ApiResponse response = authService.activateUser(activationCode);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyActivatedException e) {
            ApiResponse response = new ApiResponse(e.getMessage(), 400);
            return ResponseEntity.status(400).body(response);
        } catch (UserNotFoundException e) {
            ApiResponse response = new ApiResponse(e.getMessage(), 404);
            return ResponseEntity.status(404).body(response);
        }
    }


}