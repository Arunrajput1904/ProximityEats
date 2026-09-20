package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Emailinput {

    @Email
    String email;

}
