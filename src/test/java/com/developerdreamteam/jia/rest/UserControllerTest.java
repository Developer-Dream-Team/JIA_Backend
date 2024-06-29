package com.developerdreamteam.jia.rest;

import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
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
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Alice");
        userDTO.setLastName("Smith");
        userDTO.setPassword("password123");

        User user = new User();
        user.setId("1");
        user.setEmail("test@example.com");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setPassword("password123");
        user.setTimestamp("2024-01-01 12:00:00");

        ServiceResponse<User> serviceResponse = new ServiceResponse<>(HttpStatus.CREATED, "User created successfully", user);

        when(userService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"lastName\": \"Smith\", \"firstName\": \"Alice\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));
    }

    @Test
    void testSignup_EmailAlreadyInUse() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Alice");
        userDTO.setLastName("Smith");
        userDTO.setPassword("password123");

        ServiceResponse<User> serviceResponse = new ServiceResponse<>(HttpStatus.BAD_REQUEST, "Email is already in use", null);

        when(userService.saveUser(userDTO)).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"lastName\": \"Smith\", \"firstName\": \"Alice\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is already in use")));
    }
}
