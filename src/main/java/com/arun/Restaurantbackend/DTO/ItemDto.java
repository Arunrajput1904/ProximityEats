package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Utilis.ItemAction;
import com.arun.Restaurantbackend.Utilis.TemperatureState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto{

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotEmpty
    private String imageUrl;

@JsonIgnore
@ToString.Exclude
private MultipartFile multipartImage;


@NotNull
    @Enumerated(EnumType.STRING)
    TemperatureState state;


@NotNull
    Float max_radius_km;


    @NotNull
    @Enumerated(EnumType.STRING)
    ItemAction action;
}
