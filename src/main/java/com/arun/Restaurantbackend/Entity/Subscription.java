package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String gatewaySubId;

    private Long planId;

    @ManyToOne
    @JoinColumn(name = "plain_id")
    Plan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDate startDate;

    private LocalDate nextBillingDate;

}
