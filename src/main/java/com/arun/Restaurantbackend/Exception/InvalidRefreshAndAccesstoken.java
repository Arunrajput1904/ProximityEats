package com.arun.Restaurantbackend.Exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidRefreshAndAccesstoken extends AuthenticationException {
    public InvalidRefreshAndAccesstoken(String message) {
        super(message);
    }
}
