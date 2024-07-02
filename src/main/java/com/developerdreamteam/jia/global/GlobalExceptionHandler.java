package com.developerdreamteam.jia.global;

import com.developerdreamteam.jia.auth.exceptions.EmailSendingFailedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyActivatedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyExistsException;
import com.developerdreamteam.jia.auth.exceptions.UserNotFoundException;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ServiceResponse<?>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ServiceResponse<?> response = new ServiceResponse<>(HttpStatus.BAD_REQUEST, "The email address is already registered.", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ResponseEntity<ServiceResponse<?>> handleUserAlreadyActivatedException(UserAlreadyActivatedException ex) {
        ServiceResponse<?> response = new ServiceResponse<>(HttpStatus.BAD_REQUEST, "The account is already activated.", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ServiceResponse<?>> handleUserNotFoundException(UserNotFoundException ex) {
        ServiceResponse<?> response = new ServiceResponse<>(HttpStatus.NOT_FOUND, "The activation code is invalid or the user does not exist.", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ResponseEntity<ServiceResponse<?>> handleEmailSendingFailedException(EmailSendingFailedException ex) {
        ServiceResponse<?> response = new ServiceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send the activation email. Please try again later.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResponse<?>> handleGenericException(Exception ex) {
        ServiceResponse<?> response = new ServiceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstants.GENERIC_ERROR_MESSAGE, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
