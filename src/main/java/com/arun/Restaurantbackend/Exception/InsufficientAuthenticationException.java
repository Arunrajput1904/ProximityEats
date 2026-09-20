package com.arun.Restaurantbackend.Exception;

import org.springframework.security.core.AuthenticationException;

public class InsufficientAuthenticationException extends AuthenticationException {
    public InsufficientAuthenticationException(String message) {
        super(message);
    }
}
