package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupPayDto {

    Long userid;

    Double totalPrice=0.0;


    Long razorPay_generated_id;

    String secretKey;

    public GroupPayDto(Long id, @NotNull Double price) {
    }
}
