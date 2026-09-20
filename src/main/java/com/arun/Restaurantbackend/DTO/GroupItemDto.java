package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupItemDto {

    private Long id;

      private String name;


    private Double price;

    private Integer quantity;

    @ToString.Exclude
    @JsonIgnore
  private   GroupCartDto groupCart;

@JsonIgnore
    private User addedBy;
}
