package com.arun.Restaurantbackend.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse1 {
    
    String name;
    
    String email;
    
    String city;
    
    List<RestaurantDto> restaurantDtoList=new ArrayList<>();
    
}
