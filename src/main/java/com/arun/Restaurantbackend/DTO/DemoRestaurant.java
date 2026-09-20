package com.arun.Restaurantbackend.DTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class DemoRestaurant {

    Long id;

    String name;

    String pinCode;

    String town;

    String city;

    String state;


}
