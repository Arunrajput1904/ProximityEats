package com.arun.Restaurantbackend.DTO;


import com.arun.Restaurantbackend.Utilis.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestaurantDto implements Serializable{



    @NotNull
    private Long id;

    @NotBlank(message = "User name should not be black or empty")
    private String name;


    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private   String phoneNumber;
@NotNull
private String imageUrl;


    String town;

    String city;

    String state;

    String pinCode;

    private MenuDto menu;

    ManagerProfileDto managerProfile;

    String email;

    private StatusEnum status;
}
