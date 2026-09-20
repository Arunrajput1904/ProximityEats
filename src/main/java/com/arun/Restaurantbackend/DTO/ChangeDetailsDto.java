package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeDetailsDto {

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private   String phoneNumber;


    Long roleid;
}
