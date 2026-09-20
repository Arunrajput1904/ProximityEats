package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class changepassword {
    @Email
    @NotEmpty
    String email;

    String otp;


    String setNewPassword;

}
