package com.arun.Restaurantbackend.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class WalletDto {

    @NotNull
    Long id;

    @ToString.Exclude
    UserDto user;
    @NotNull
    int balance;


}
