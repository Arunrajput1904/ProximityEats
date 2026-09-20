package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Utilis.BundleStatus;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BundleResponse {

    String restaurantName;

    Double price;

    Long RestaurantDistanceFromYou;

    Long bundleId;


   BundleStatus bundleStatus;

}
