package com.arun.Restaurantbackend.DTO;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class loginUser {

    @Email
    @NotEmpty
    String email;

    private   String phoneNumber;
    @NotBlank(message = "Password Should be Atleast  of 8 length")
    @Size(min = 8,message = "Pasword should ne atleast 8 length")
    String password;

}
