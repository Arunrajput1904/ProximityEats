package com.arun.Restaurantbackend.DTO;

import com.arun.Restaurantbackend.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class GroupCartDto {

private Long id ;


@NotNull
@JsonIgnore
    private User hostUser;

    @JsonIgnore
    private List<User>participates;


    private String  uniqueId;

    private Long societyId;


    private LocalDateTime expiresAt;

@JsonIgnore
   private RestaurantDto restaurant;

   private LocalDateTime paymentAt;

   private List<GroupItemDto>groupItemList;
    private double deliveryFees;
}
