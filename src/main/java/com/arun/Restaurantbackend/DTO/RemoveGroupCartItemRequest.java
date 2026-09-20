package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveGroupCartItemRequest {

    @NotNull
    Long groupCartId;
    @NotNull
    Long groupCartItemId;
}
