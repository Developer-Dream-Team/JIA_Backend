package com.developerdreamteam.jia.auth.service;

import com.developerdreamteam.jia.auth.exceptions.EmailSendingFailedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyExistsException;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.commons.EmailServiceImpl;
import com.developerdreamteam.jia.constants.MessageConstants;
import com.developerdreamteam.jia.util.EmailContentGenerator;
import com.developerdreamteam.jia.util.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    @Value("${app.base.url}")
    private String baseUrl;

    @Transactional
    public ServiceResponse<UserResponseDTO> saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException(MessageConstants.EMAIL_IN_USE_MESSAGE);
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setLastName(userDTO.getLastName());
        user.setFirstName(userDTO.getFirstName());
        user.setPassword(passwordEncoderService.encodePassword(userDTO.getPassword()));
        user.setActive(false);
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());
        user.setActivationCode(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        String activationLink = baseUrl + "api/v1/users/signup/confirmation?success=" + user.getActivationCode();

        String emailContent = EmailContentGenerator.generateActivationEmailContent(activationLink);

        try {
            emailService.sendSimpleMessage(savedUser.getEmail(), "Account Activation", emailContent);
        } catch (Exception e) {
            throw new EmailSendingFailedException(MessageConstants.EMAIL_SENDING_FAILED_MESSAGE, e);
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.isActive(),
                savedUser.getTimestamp()
        );

        return new ServiceResponse<>(HttpStatus.CREATED, MessageConstants.USER_CREATION_SUCCESS_MESSAGE, userResponseDTO);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
