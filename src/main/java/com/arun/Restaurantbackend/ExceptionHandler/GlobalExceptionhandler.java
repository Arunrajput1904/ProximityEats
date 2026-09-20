package com.arun.Restaurantbackend.ExceptionHandler;

import com.arun.Restaurantbackend.Advice.Apierror;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Exception.IllegalAccessException;
import com.arun.Restaurantbackend.Exception.IllegalArgumentException;
//import org.apache.coyote.BadRequestException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionhandler {


//    @ExceptionHandler(ResourceNoFoundException.class)
//    ResponseEntity<Apierror> NoResourceFoundException(ResourceNoFoundException exception){
//        Apierror apierror=Apierror.builder().status(404).reason(HttpStatus.NOT_FOUND).message(exception.getMessage()).build();
//
//        return new ResponseEntity<>(apierror,HttpStatus.NOT_FOUND);
//    }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Apierror> AccessDeniedException(AccessDeniedException exception){
        Apierror apierror=Apierror.builder().status(403).reason(HttpStatus.FORBIDDEN).message(exception.getMessage()).build();

        return new ResponseEntity<>(apierror,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalPaymentException.class)
    ResponseEntity<Apierror> IllegalPaymentException(IllegalPaymentException exception){
        Apierror apierror=Apierror.builder().status(402).reason(HttpStatus.PAYMENT_REQUIRED).message(exception.getMessage()).build();

        return new ResponseEntity<>(apierror,HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<Apierror> Authiecationexception(AuthenticationException exception){
        Apierror apierror=Apierror.builder().status(401).reason(HttpStatus.UNAUTHORIZED).message(exception.getMessage()).build();

        return new ResponseEntity<>(apierror,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ResourceAlreadyExistsException.class,ConflictException.class})
    ResponseEntity<Apierror> ResourceAlreadyExistsException(RuntimeException exception){
        Apierror apierror=Apierror.builder().status(409).reason(HttpStatus.CONFLICT).message(exception.getMessage()).build();
        return new ResponseEntity<>(apierror,HttpStatus.CONFLICT);
    }


    @ExceptionHandler(IllegalAccessException.class)
    ResponseEntity<Apierror> IllegalAccessException(IllegalAccessException exception){
        Apierror apierror=Apierror.builder().status(403).reason(HttpStatus.FORBIDDEN).message(exception.getMessage()).build();

        return new ResponseEntity<>(apierror,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, BadRequestException.class, FileNotUploadException.class})
    public ResponseEntity<Apierror> handleIllegalArgument(RuntimeException  ex) {

        Apierror apierror = Apierror.builder()
                .status(400)
                .reason(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apierror);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Apierror> ConstraintViolationExceptions(ConstraintViolationException ex) {

        Apierror apierror = Apierror.builder()
                .status(400)
                .reason(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }



    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Apierror> handleIllegalState(IllegalStateException ex) {

        Apierror apierror = Apierror.builder()
                .status(409)
                .reason(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apierror);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Apierror> entityexception(UnprocessableEntityException ex) {

        Apierror apierror = Apierror.builder()
                .status(422)
                .reason(HttpStatus.UNPROCESSABLE_ENTITY)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apierror);
    }

//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Apierror> RuntimeExceptionss(RuntimeException ex) {
//
//        Apierror apierror = Apierror.builder()
//                .status(500)
//                .reason(HttpStatus.INTERNAL_SERVER_ERROR)
//                .message(ex.getMessage())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apierror);
//    }









}
