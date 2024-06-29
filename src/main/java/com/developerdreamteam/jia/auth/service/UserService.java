package com.developerdreamteam.jia.auth.service;


import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.util.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    public ServiceResponse<User> saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return new ServiceResponse<>(HttpStatus.BAD_REQUEST, "Email is already in use", null);
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setLastName(userDTO.getLastName());
        user.setFirstName(userDTO.getFirstName());
        user.setPassword(userDTO.getPassword());
        user.setTimestamp(TimestampUtil.getCurrentTimestamp());

        User savedUser = userRepository.save(user);
        return new ServiceResponse<>(HttpStatus.CREATED, "User created successfully", savedUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findById(email);
    }
}
