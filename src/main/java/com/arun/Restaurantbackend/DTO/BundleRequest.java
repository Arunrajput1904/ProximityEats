package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Utilis.BundleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BundleRequest {



    String restaurantName;
        Long bundleId;
        BundleStatus bundleStatus;



}
