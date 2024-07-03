package com.developerdreamteam.jia.service;

import com.developerdreamteam.jia.auth.exceptions.EmailSendingFailedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyExistsException;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.AuthRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.service.AuthService;
import com.developerdreamteam.jia.commons.EmailServiceImpl;
import com.developerdreamteam.jia.constants.MessageConstants;
import com.developerdreamteam.jia.util.TimestampUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "baseUrl", "http://localhost:8080/");
    }

    @Test
    void testSaveUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(encodedPassword);

        when(authRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(authRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.set_id("1");
            user.setActivationCode(UUID.randomUUID().toString());
            return user;
        });

        doNothing().when(emailService).sendSimpleMessage(
                eq("helper@example.com"),
                eq("Account Activation"),
                contains("http://localhost:8080/api/v1/auth/signup/confirmation?success=")
        );

        ServiceResponse<UserResponseDTO> response = authService.saveUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(MessageConstants.USER_CREATION_SUCCESS_MESSAGE, response.getMessage());
        assertEquals("helper@example.com", response.getData().getEmail());
        assertEquals("Jones", response.getData().getFirstName());
        assertEquals("James", response.getData().getLastName());
        assertNotNull(response.getData().getTimestamp());
        assertNotNull(response.getData().getId());

        verify(passwordEncoder).encode("password123");
    }

    @Test
    void testSaveUser_EmailAlreadyInUse() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("helper@example.com");
        userDTO.setFirstName("Jones");
        userDTO.setLastName("James");
        userDTO.setPassword("password123");

        when(authRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.saveUser(userDTO);
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
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(encodedPassword);

        when(authRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(authRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.set_id("1");
            user.setActivationCode(UUID.randomUUID().toString());
            return user;
        });

        doThrow(new RuntimeException("Email sending failed")).when(emailService).sendSimpleMessage(
                eq("helper@example.com"),
                eq("Account Activation"),
                contains("http://localhost:8080/api/v1/auth/signup/confirmation?success=")
        );

        EmailSendingFailedException exception = assertThrows(EmailSendingFailedException.class, () -> {
            authService.saveUser(userDTO);
        });

        assertEquals(MessageConstants.EMAIL_SENDING_FAILED_MESSAGE, exception.getMessage());
    }

    @Test
    void testFindUserByEmail() {
        User user = new User();
        user.set_id("1");
        user.setEmail("helper@example.com");
        user.setFirstName("Brandon");
        user.setLastName("Phillipe");
        user.setPassword("password123");
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());

        when(authRepository.findByEmail("helper@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = authService.findUserByEmail("helper@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user.get_id(), foundUser.get().get_id());
    }
}
