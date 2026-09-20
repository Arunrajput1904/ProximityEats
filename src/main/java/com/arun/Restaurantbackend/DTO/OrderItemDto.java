package com.arun.Restaurantbackend.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItemDto {

    Long id;

    String  itemName;

    Double itemPrice;

    @ToString.Exclude
            @JsonIgnore
    OrderDto order;

}
