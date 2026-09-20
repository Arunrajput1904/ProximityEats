package com.arun.Restaurantbackend.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse2{

    String name;

    String email;



    List<DemoRestaurant>restaurantDtoList=new ArrayList<>();

}