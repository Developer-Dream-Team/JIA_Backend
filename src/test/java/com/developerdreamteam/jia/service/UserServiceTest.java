package com.developerdreamteam.jia.service;

import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.service.UserService;
import com.developerdreamteam.jia.auth.util.TimestampUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("1");
            return user;
        });

        ServiceResponse<UserResponseDTO> response = userService.saveUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("User created successfully", response.getMessage());
        assertEquals("test@example.com", response.getData().getEmail());
        assertEquals("Jones", response.getData().getFirstName());
        assertEquals("James", response.getData().getLastName());
        assertNotNull(response.getData().getTimestamp());
    }

    @Test
    void testSaveUser_EmailAlreadyInUse() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        ServiceResponse<UserResponseDTO> response = userService.saveUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertNull(response.getData());
        assertEquals("Email is already in use", response.getMessage());
    }

    @Test
    void testFindUserByEmail() {
        User user = new User();
        user.setId("1");
        user.setEmail("test@example.com");
        user.setFirstName("Brandon");
        user.setLastName("Phillipe");
        user.setPassword("password123");
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());

        when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }
}
