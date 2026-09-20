package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(schema = "restaurant_service")
public class Menu {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "restaurant_id")
  Restaurant restaurant;

    @Size(min = 3, message = "At least 3 menu items required")
    @OneToMany(mappedBy = "menu",fetch = FetchType.EAGER, cascade = CascadeType.ALL,orphanRemoval = true)
    List<Item>itemList=new ArrayList<>();
}
