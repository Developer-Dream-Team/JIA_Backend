package com.developerdreamteam.jia.rest;

import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.rest.UserController;
import com.developerdreamteam.jia.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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

        when(userService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/users/signup")
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

        when(userService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"helper@example.com\", \"lastName\": \"Smith\", \"firstName\": \"Alice\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is already in use")));
    }
}
