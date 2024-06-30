package com.developerdreamteam.jia.global;

import com.developerdreamteam.jia.auth.exceptions.EmailSendingFailedException;
import com.developerdreamteam.jia.auth.exceptions.UserAlreadyExistsException;
import com.developerdreamteam.jia.auth.response.ServiceResponse;
import com.developerdreamteam.jia.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ServiceResponse<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ServiceResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ServiceResponse<?> handleEmailSendingFailedException(EmailSendingFailedException ex) {
        return new ServiceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ServiceResponse<?> handleGenericException(Exception ex) {
        return new ServiceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstants.GENERIC_ERROR_MESSAGE, null);
    }
}
