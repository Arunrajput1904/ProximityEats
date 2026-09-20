package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class SocietyEdge {


    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @ManyToOne
    SocietyN society1;

    @ManyToOne
    SocietyN society2;

@NotNull
    int distance;



}
