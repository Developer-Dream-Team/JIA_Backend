package com.developerdreamteam.jia.auth.exceptions;

public class EmailSendingFailedException extends RuntimeException {
    public EmailSendingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}