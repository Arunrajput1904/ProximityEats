package com.arun.Restaurantbackend.Exception;

import org.springframework.security.core.AuthenticationException;

public class SessionAuthenticationException extends AuthenticationException {
    public SessionAuthenticationException(String message) {
        super(message);
    }
}
