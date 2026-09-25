package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.BundleStatus;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String zoneName;



    @OneToOne
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;


    String optimalRoute;



    @Column(nullable = false)
    Double Price;
    @Enumerated(EnumType.STRING)
    BundleStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;

    @OneToMany(orphanRemoval = true,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    List<OrderBundle> orderBundles=new ArrayList<>();


    Integer itemCount=0;


    @OneToOne
    @JoinColumn(name = "delivery_id")
    DeliveryBoy deliveryBoy;



    @OneToOne
    @JoinColumn(name = "cancel_id")
    CancelEntity cancelEntity;


    LocalDateTime lastUpdateTime;
}
