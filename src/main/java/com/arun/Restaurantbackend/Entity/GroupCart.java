package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class GroupCart{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "host_user_id",nullable = false)
    @ToString.Exclude
private     User hostUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_cart_users",
            joinColumns = @JoinColumn(name = "CartGroup"),
            inverseJoinColumns = @JoinColumn(name ="user_id" )
    )
    @ToString.Exclude
    private List<User> participates=new ArrayList<>();


    @Column(nullable = false,unique = true)
private     String  uniqueId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


    private Long societyId;

    private LocalDateTime paymentAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
private     GroupCartStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "groupCart",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<GroupItem>groupItemList=new ArrayList<>();

    private double deliveryFees;


    private Long orderId;


    @OneToMany(mappedBy = "groupCart" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<GroupOtpTrack>groupOtpTracks=new ArrayList<>();

}
