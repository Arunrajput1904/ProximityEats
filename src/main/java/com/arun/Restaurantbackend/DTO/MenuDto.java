package com.arun.Restaurantbackend.DTO;


import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuDto implements Serializable {


    Long id;



    List<ItemDto>itemList=new ArrayList<>();
}
