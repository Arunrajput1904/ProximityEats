package com.arun.Restaurantbackend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class  UserDto {



   private Long id;

    @NotBlank(message = "User name should not be black or empty")
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


private     RoleDto role;


private  boolean active=false;
@JsonIgnore
private  WalletDto wallet;


}


