package com.arun.Restaurantbackend.DTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Userprofiledto {

Long id;

Long userid;


    String societyName;

    String city;

    String state;

    String pinCode;



}
