package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Entity.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Address {

    Long id;


    String hometown;

    String city;

    String state;

    String pinCode;

    List<User> userList=new ArrayList<>();
}
