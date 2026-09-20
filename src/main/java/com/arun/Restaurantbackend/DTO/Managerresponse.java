package com.arun.Restaurantbackend.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Managerresponse {


    String name;
    String email;

    List<RestaurantDto> restaurantDtoList=new ArrayList<>();

}
