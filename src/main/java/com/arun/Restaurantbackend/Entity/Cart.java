package com.arun.Restaurantbackend.Entity;

import com.arun.Restaurantbackend.Utilis.CartEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString
@Table(schema = "order_details")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long userId;


    @Enumerated(EnumType.STRING)
    CartEnum status;


    Long restaurantid;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "cart" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<Cartitem> list=new ArrayList<>();



    @CreationTimestamp
    LocalDateTime lastUpdateTime;

}
