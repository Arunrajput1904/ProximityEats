package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Utilis.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public  class  DeliveryBoyDto{




    private Long id;

    private UserDto userDto;

 String city;

 String state;

 String pincode;

 String town;

    private StatusEnum status;

    String vehicleNumber;




}
