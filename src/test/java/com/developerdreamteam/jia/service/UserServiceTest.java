package com.developerdreamteam.jia.service;

import com.developerdreamteam.jia.auth.exceptions.EmailSendingFailedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyExistsException;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.service.PasswordEncoderService;
import com.developerdreamteam.jia.auth.service.UserService;
import com.developerdreamteam.jia.commons.EmailServiceImpl;
import com.developerdreamteam.jia.constants.MessageConstants;
import com.developerdreamteam.jia.util.TimestampUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "baseUrl", "http://localhost:8080/");
    }

    @Test
    void testSaveUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        String encodedPassword = "encodedPassword";
        when(passwordEncoderService.encodePassword(userDTO.getPassword())).thenReturn(encodedPassword);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("1");
            user.setActivationCode(UUID.randomUUID().toString());
            return user;
        });

        doNothing().when(emailService).sendSimpleMessage(
                eq("helper@example.com"),
                eq("Account Activation"),
                contains("http://localhost:8080/api/v1/users/signup/confirmation?success=")
        );

        ServiceResponse<UserResponseDTO> response = userService.saveUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(MessageConstants.USER_CREATION_SUCCESS_MESSAGE, response.getMessage());
        assertEquals("helper@example.com", response.getData().getEmail());
        assertEquals("Jones", response.getData().getFirstName());
        assertEquals("James", response.getData().getLastName());
        assertNotNull(response.getData().getTimestamp());
        assertNotNull(response.getData().getId());

        verify(passwordEncoderService).encodePassword("password123");
    }

    @Test
    void testSaveUser_EmailAlreadyInUse() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.saveUser(userDTO);
        });

        assertEquals(MessageConstants.EMAIL_IN_USE_MESSAGE, exception.getMessage());
    }

    @Test
    void testSaveUser_EmailSendingFails() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        String encodedPassword = "encodedPassword";
        when(passwordEncoderService.encodePassword(userDTO.getPassword())).thenReturn(encodedPassword);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("1");
            user.setActivationCode(UUID.randomUUID().toString());
            return user;
        });

        doThrow(new RuntimeException("Email sending failed")).when(emailService).sendSimpleMessage(
                eq("helper@example.com"),
                eq("Account Activation"),
                contains("http://localhost:8080/api/v1/users/signup/confirmation?success=")
        );

        EmailSendingFailedException exception = assertThrows(EmailSendingFailedException.class, () -> {
            userService.saveUser(userDTO);
        });

        assertEquals(MessageConstants.EMAIL_SENDING_FAILED_MESSAGE, exception.getMessage());
    }

    @Test
    void testFindUserByEmail() {
        User user = new User();
        user.setId("1");
        user.setEmail("helper@example.com");
        user.setFirstName("Brandon");
        user.setLastName("Phillipe");
        user.setPassword("password123");
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());

        when(userRepository.findByEmail("helper@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByEmail("helper@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }
}
