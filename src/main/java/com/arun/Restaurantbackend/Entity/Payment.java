package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Payment {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long orderRefId;

    @NotNull
    @Enumerated(EnumType.STRING)
    PaymentEnum status;


    @Column(unique = true)
    Long counter;



}
