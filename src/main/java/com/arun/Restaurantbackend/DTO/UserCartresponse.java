package com.arun.Restaurantbackend.DTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor
@Builder
public class UserCartresponse {
    String username;

    CartDto cart;

    Double total;
}
