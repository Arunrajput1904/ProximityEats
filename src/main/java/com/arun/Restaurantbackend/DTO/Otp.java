package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Otp {
    @NotEmpty
    String otp;
}
