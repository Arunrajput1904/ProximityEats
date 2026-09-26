package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.DTO.BundleDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder
@Getter
@Setter
public class OrderBundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

   @OneToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "order_id")
           @JsonIgnore
    Order order;

    private Integer positionInRoute;

    @ManyToOne
    @JoinColumn(name = "bundle_id")
            @ToString.Exclude
            @JsonIgnore
    Bundle bundle;
}
