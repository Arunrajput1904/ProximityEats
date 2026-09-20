package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor

public class AddGroupCartItemRequest {



    @NotNull
    Long menuitemId;

    @NotNull
    Long groupCartId;
}
