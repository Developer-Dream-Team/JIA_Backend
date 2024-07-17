package com.developerdreamteam.jia.rest;

import com.developerdreamteam.jia.auth.model.dto.ResendVerificationEmailDTO;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.AuthRepository;
import com.developerdreamteam.jia.auth.response.ApiResponse;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.rest.AuthController;
import com.developerdreamteam.jia.auth.service.AuthService;
import com.developerdreamteam.jia.commons.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testSignup_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Alice");
        userDTO.setLastName("Smith");
        userDTO.setPassword("password123");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId("1");
        userResponseDTO.setEmail("helper@example.com");
        userResponseDTO.setFirstName("Alice");
        userResponseDTO.setLastName("Smith");
        userResponseDTO.setActive(false);
        userResponseDTO.setTimestamp("2024-01-01 12:00:00");

        ServiceResponse<UserResponseDTO> serviceResponse = new ServiceResponse<>(HttpStatus.CREATED, "User created successfully", userResponseDTO);

        when(authService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"helper@example.com\", \"lastName\": \"Smith\", \"firstName\": \"Alice\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.email", is("helper@example.com")))
                .andExpect(jsonPath("$.data.active", is(false)))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void testSignup_EmailAlreadyInUse() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Alice");
        userDTO.setLastName("Smith");
        userDTO.setPassword("password123");

        ServiceResponse<UserResponseDTO> serviceResponse = new ServiceResponse<>(HttpStatus.BAD_REQUEST, "Email is already in use", null);

        when(authService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"helper@example.com\", \"lastName\": \"Smith\", \"firstName\": \"Alice\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is already in use")));
    }


    @Test
    void testResendVerificationEmail_Success() throws Exception {
        String email = "helper@example.com";
        User user = new User();
        user.setEmail(email);
        user.setActive(false);
        user.setActivationExpiry(LocalDateTime.now().minusDays(1));

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(authRepository.save(any(User.class))).thenReturn(user);

        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        ResendVerificationEmailDTO resendVerificationEmailDTO = new ResendVerificationEmailDTO();
        resendVerificationEmailDTO.setEmail(email);

        ApiResponse apiResponse = new ApiResponse("Verification email resent successfully", HttpStatus.OK.value());
        when(authService.resendVerificationEmail(any(ResendVerificationEmailDTO.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"helper@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Verification email resent successfully"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()));
    }

    @Test
    public void testResendVerificationEmail_UserAlreadyActive() throws Exception {
        String email = "active@example.com";
        User user = new User();
        user.setEmail(email);
        user.setActive(true);

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ApiResponse apiResponse = new ApiResponse("User is already activated", HttpStatus.BAD_REQUEST.value());
        when(authService.resendVerificationEmail(any(ResendVerificationEmailDTO.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"active@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User is already activated"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testResendVerificationEmail_UserNotFound() throws Exception {
        String email = "nonexistent@example.com";

        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        ApiResponse apiResponse = new ApiResponse("User not found", HttpStatus.NOT_FOUND.value());
        when(authService.resendVerificationEmail(any(ResendVerificationEmailDTO.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"nonexistent@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()));
    }
}
