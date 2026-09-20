package com.arun.Restaurantbackend.Exception;

public class InvalidElementException extends RuntimeException {

    public InvalidElementException(String invalidName) {
        super(invalidName);
    }
}


