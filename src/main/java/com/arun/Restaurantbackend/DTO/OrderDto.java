package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Utilis.OrderEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    Long id;



    String estimatetime;
    Long estimatekm;

    @ToString.Exclude
    List<OrderItemDto> itemList=new ArrayList<>();

    @ToString.Exclude
    Double orderItemCharge;

    @ToString.Exclude
    Double deliveryFees;


    Double totalGrant;

    OrderEnum status;

    StringBuilder usertorestloc;
    StringBuilder resttoboyloc;


}
