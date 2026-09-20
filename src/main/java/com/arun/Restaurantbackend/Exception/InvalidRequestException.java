package com.arun.Restaurantbackend.Exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String invalidName) {
        super(invalidName);
    }
}
