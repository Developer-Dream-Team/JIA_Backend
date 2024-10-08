package com.developerdreamteam.jia.auth.service;

import com.developerdreamteam.jia.auth.exceptions.*;
import com.developerdreamteam.jia.auth.model.dto.ResendVerificationEmailDTO;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserDetailsDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.AuthRepository;
import com.developerdreamteam.jia.auth.response.ApiResponse;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.role.Roles;
import com.developerdreamteam.jia.auth.security.custom.CustomUserDetails;
import com.developerdreamteam.jia.commons.EmailServiceImpl;
import com.developerdreamteam.jia.constants.MessageConstants;
import com.developerdreamteam.jia.util.EmailContentGenerator;
import com.developerdreamteam.jia.util.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {


    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.base.url}")
    private String baseUrl;

    @Transactional
    public ServiceResponse<UserResponseDTO> saveUser(UserDTO userDTO) {
        if (authRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException(MessageConstants.EMAIL_IN_USE_MESSAGE);
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setLastName(userDTO.getLastName());
        user.setFirstName(userDTO.getFirstName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActive(false);
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());
        user.setActivationCode(UUID.randomUUID().toString());
        user.setRole(String.valueOf(Roles.ROLE_USER));
        user.setActivationExpiry(TimestampUtil.TimeLimit());

        User savedUser = authRepository.save(user);

        String activationLink = baseUrl + "api/v1/auth/signup/confirmation?success=" + user.getActivationCode();

        String emailContent = EmailContentGenerator.generateActivationEmailContent(activationLink);

        try {
            emailService.sendSimpleMessage(savedUser.getEmail(), "Account Activation", emailContent);
        } catch (Exception e) {
            throw new EmailSendingFailedException(MessageConstants.EMAIL_SENDING_FAILED_MESSAGE, e);
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                savedUser.get_id(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.isActive(),
                savedUser.getTimestamp()
        );

        return new ServiceResponse<>(HttpStatus.CREATED, MessageConstants.USER_CREATION_SUCCESS_MESSAGE, userResponseDTO);
    }

    @Transactional
    public ApiResponse activateUser(String activationCode) {
        Optional<User> userOptional = authRepository.findByActivationCode(activationCode);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isActive()) {
                throw new UserAlreadyActivatedException("Account is already activated");
            }
            else if (user.getActivationExpiry().isBefore((LocalDateTime.now()))) {
                throw new ActivationCodeExpiredException("Activation code has expired");
            }

            user.setActive(true);
            user.setActivationCode(null);
            authRepository.save(user);

            return new ApiResponse("Account activated successfully", HttpStatus.OK.value());
        } else {
            throw new UserNotFoundException("Invalid activation code");
        }
    }

    @Transactional
    public ApiResponse resendVerificationEmail(ResendVerificationEmailDTO resendVerificationEmailDTO) {
        Optional<User> userOptional = authRepository.findByEmail(resendVerificationEmailDTO.getEmail());

        if (userOptional.isPresent() && !userOptional.get().isActive()) {
            User user = userOptional.get();

            if (user.getActivationExpiry() == null || LocalDateTime.now().isAfter(user.getActivationExpiry())) {
                user.setActivationCode(UUID.randomUUID().toString());
                user.setActivationExpiry(TimestampUtil.TimeLimit());
                authRepository.save(user);

                String activationLink = baseUrl + "/api/v1/auth/signup/confirmation?success=" + user.getActivationCode();
                String emailContent = EmailContentGenerator.generateActivationEmailContent(activationLink);

                try {
                    emailService.sendSimpleMessage(user.getEmail(), "Account Activation", emailContent);
                } catch (Exception e) {
                    throw new EmailSendingFailedException(MessageConstants.EMAIL_SENDING_FAILED_MESSAGE, e);
                }
                return new ApiResponse("Verification email resent successfully", HttpStatus.OK.value());
            } else {
                return new ApiResponse("Activation email already sent recently. Please check your email.", HttpStatus.OK.value());
            }
        } else {
            throw new UserNotFoundException("user not found or already activated");
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUserDetails = authRepository.findByEmail(username);

        return optionalUserDetails.map(user -> {
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                    user.get_id(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    Collections.singletonList(user.getRole())
            );
            return new CustomUserDetails(user, userDetailsDTO);
        }).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}
