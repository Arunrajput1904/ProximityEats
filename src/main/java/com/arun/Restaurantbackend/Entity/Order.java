package com.arun.Restaurantbackend.Entity;

import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(schema = "restaurant_service")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    LocalDateTime estimatedeliverytime;

    @NotEmpty
    String estimatetime;

    @NotNull
    Double estimatekm;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;


    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "address_id")
    Userprofile address;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
            @JsonIgnore
            @ToString.Exclude
    List<OrderItem> itemList=new ArrayList<>();

    BigDecimal orderItemCharge;

    BigDecimal deliveryFees;


    @Enumerated(EnumType.STRING)
    OrderEnum status;


    BigDecimal totalGrant;



    LocalDateTime orderAcceptTime;


    LocalDateTime lastpaymentTime;

    LocalDateTime lastUpdateTime;


    @CreationTimestamp
    LocalDateTime createdAt;


    StringBuilder usertorestloc;

    StringBuilder resttoboyloc;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cancel_id")
    CancelEntity cancel;

    @Enumerated(EnumType.STRING)
    OrderType orderType;





}