package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Userobject {




    private String name;


    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private   String phoneNumber;

    @Email
    @NotEmpty
    String email;

    String password;

    Long roleid;
}