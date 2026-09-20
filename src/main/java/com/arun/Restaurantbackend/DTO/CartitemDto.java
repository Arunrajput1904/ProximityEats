package com.arun.Restaurantbackend.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartitemDto {


    String name;

    Double price;

    Integer quantity;

}
