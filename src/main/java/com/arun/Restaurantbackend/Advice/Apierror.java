package com.arun.Restaurantbackend.Advice;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Apierror {

    HttpStatus reason;

    String message;

    int status;
}
