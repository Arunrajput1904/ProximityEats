package com.arun.Restaurantbackend.Exception;

public class ResourceNoFoundException extends RuntimeException {
    public ResourceNoFoundException(String message) {
        super(message);
    }
}
