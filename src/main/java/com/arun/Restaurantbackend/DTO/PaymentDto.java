package com.arun.Restaurantbackend.DTO;


import com.arun.Restaurantbackend.Utilis.OrderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentDto {

    Long id;

    Long orderRefId;

    @Enumerated(EnumType.STRING)
    OrderEnum status;



}
