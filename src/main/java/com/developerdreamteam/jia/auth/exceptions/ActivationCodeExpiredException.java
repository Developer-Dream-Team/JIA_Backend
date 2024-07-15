package com.developerdreamteam.jia.auth.exceptions;

public class ActivationCodeExpiredException extends RuntimeException{
    public ActivationCodeExpiredException(String message) {
        super(message);
    }
}
