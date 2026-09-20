package com.arun.Restaurantbackend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Managerrestprofile {

    Long id;


    String state;

    String city;



    @JsonIgnore
    RestaurantDto restaurant;


}