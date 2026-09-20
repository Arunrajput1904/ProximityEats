package com.arun.Restaurantbackend.DTO;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deliveryboydashboard {

    String deliveryBoyname;

    List<OrderDto> list=new ArrayList<>();

    String boystatus;
}
