package com.arun.Restaurantbackend.DTO;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {

    Long id;


    Long userId;


    Long restaurantid;

    List<CartitemDto>list=new ArrayList<>();


    @CreationTimestamp
    LocalDateTime lastUpdateTime;

}
