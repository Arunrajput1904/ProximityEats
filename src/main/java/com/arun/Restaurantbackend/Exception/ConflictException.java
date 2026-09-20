package com.arun.Restaurantbackend.Exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String msg){
        super(msg);
    }
}
