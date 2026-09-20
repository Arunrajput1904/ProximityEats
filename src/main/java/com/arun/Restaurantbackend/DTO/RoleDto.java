package com.arun.Restaurantbackend.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleDto {

    @NotNull
    Long id;

    @NotBlank(message = "Role should not be black , empty")
    String type;


    UserDto userDto;

}
