package com.arun.Restaurantbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscribeRequest {

    String idempotencyKey;
    Long userId;
    Long planId;

}
