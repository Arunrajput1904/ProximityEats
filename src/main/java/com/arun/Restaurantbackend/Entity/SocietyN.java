package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "society_n")
public class SocietyN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @NotEmpty
    @Column(unique = true)
    String SocietyName;



    String zoneName;
}

