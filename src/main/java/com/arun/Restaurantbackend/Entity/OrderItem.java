package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(schema = "restaurant_service")
public class OrderItem {


    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotEmpty
    String  itemName;

    @NotNull
    BigDecimal itemPrice;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "order_id")
    Order order;

    @NotNull
    int quantity;




}
