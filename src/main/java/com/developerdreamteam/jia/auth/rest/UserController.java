package com.developerdreamteam.jia.auth.rest;

import com.developerdreamteam.jia.auth.exceptions.UserAlreadyActivatedException;
import com.developerdreamteam.jia.auth.exceptions.UserNotFoundException;
import com.developerdreamteam.jia.auth.model.dto.UserDTO;
import com.developerdreamteam.jia.auth.model.dto.UserResponseDTO;
import com.developerdreamteam.jia.auth.repository.UserRepository;
import com.developerdreamteam.jia.auth.response.ApiResponse;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<ApiResponse> confirmSignup(@RequestParam("success") String activationCode) {
        try {
            ApiResponse response = userService.activateUser(activationCode);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyActivatedException e) {
            ApiResponse response = new ApiResponse(e.getMessage(), 400);
            return ResponseEntity.status(400).body(response);
        } catch (UserNotFoundException e) {
            ApiResponse response = new ApiResponse(e.getMessage(), 404);
            return ResponseEntity.status(404).body(response);
        }
    }

}
