package com.arun.Restaurantbackend.DTO;


import com.arun.Restaurantbackend.Utilis.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;
    private String userId;
    private String gatewaySubId;
    private String planId;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate nextBillingDate;
}