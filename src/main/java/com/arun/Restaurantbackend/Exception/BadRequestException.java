package com.arun.Restaurantbackend.Exception;

public class BadRequestException extends RuntimeException{
    public  BadRequestException(String msg){
        super(msg);
    }
}
