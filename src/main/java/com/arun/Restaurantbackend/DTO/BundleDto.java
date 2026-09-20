package com.arun.Restaurantbackend.DTO;


import com.arun.Restaurantbackend.Utilis.BundleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BundleDto {


    Long id;

    String zoneName;


    String optimalRoute;

   Double Price;

   BundleStatus status;

   LocalDateTime createdAt;

   List<OrderBundleDto> orderBundles;

   List<StopDto>stopList= new ArrayList<>();



}
