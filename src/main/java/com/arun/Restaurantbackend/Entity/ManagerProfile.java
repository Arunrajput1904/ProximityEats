package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(schema = "user_service")
public class ManagerProfile{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    User user;

    @NotEmpty
    String state;

    @NotEmpty
    String city;



}