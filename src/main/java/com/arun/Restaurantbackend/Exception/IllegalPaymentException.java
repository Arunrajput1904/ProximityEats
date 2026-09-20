package com.arun.Restaurantbackend.Exception;

public class IllegalPaymentException extends RuntimeException {

    public IllegalPaymentException(String invalidName) {
        super(invalidName);
    }
}
