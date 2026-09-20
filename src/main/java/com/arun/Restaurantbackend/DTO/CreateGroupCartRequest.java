package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateGroupCartRequest {





    @NotNull
    Long restaurantId;


}
