package com.arun.Restaurantbackend.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminResponseDto {

    String adminName;

    List<RestaurantDto> restaurant=new ArrayList<>();
}
