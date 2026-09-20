package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "order_details")
public class Cartitem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    Long itemid;


    @ManyToOne
    @JoinColumn(name = "cart_id",nullable = false)
    Cart cart;

    @NotEmpty
    String name;

    @NotNull
    Double price;

    @NotNull
    Integer quantity;
}
