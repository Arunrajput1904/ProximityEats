package com.arun.Restaurantbackend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    @NotNull
    Long id;

    @NotBlank(message = "Title is not empty/black")
    String title;


    @ToString.Exclude
    List<RoleDto> roleDtoList=new ArrayList<>();

}
