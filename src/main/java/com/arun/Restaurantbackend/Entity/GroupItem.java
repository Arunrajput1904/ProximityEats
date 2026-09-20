package com.arun.Restaurantbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


@NotNull
    String name;

    @NotNull
    Double price;

    @NotNull
    Integer quantity;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "group_cart_id")
    GroupCart groupCart;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "added_by_user_id", nullable = false)
    private User addedBy;


    @NotNull
    Boolean isConfirmed=false;
}
