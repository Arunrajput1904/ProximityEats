package com.arun.Restaurantbackend.Exception;

public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException(String msg){
        super(msg);
    }
}
