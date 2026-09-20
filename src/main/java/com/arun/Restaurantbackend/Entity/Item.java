package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.ItemAction;
import com.arun.Restaurantbackend.Utilis.TemperatureState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
@Builder
@Table(schema = "restaurant_service")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotEmpty
    String name;

    @NotNull
    Double price;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "menu_id")
    Menu menu;


    private String imageUrl;




    @NotNull
    @Enumerated(EnumType.STRING)
    TemperatureState state;


    @NotNull
    Float max_radius_km;



    @Enumerated(EnumType.STRING)
    ItemAction action;

}
