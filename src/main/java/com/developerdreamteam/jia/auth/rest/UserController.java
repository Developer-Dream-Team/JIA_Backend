package com.developerdreamteam.jia.auth.rest;

import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.model.entity.User;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResponse<UserResponseDTO>> signup(@RequestBody UserDTO userDTO) {
        ServiceResponse<UserResponseDTO> response = userService.saveUser(userDTO);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/signup/confirmation")
    public ResponseEntity<String> confirmSignup(@RequestParam("success") String activationCode) {
        Optional<User> userOptional = userRepository.findByActivationCode(activationCode);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(true);
            userRepository.save(user);
            return ResponseEntity.ok("Account activated successfully");
        } else {
            return ResponseEntity.status(404).body("Invalid activation code");
        }
    }
}
