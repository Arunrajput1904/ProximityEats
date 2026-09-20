package com.arun.Restaurantbackend.Entity;

import com.arun.Restaurantbackend.Utilis.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(schema = "user_service")
public  class  DeliveryBoy {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @NotEmpty
    String pincode;

    @NotEmpty
    String town;

    @NotEmpty
    String city;

    @NotEmpty
    String state;


    @NotEmpty
            @Column(unique = true)
    String vehicleNumber;


    @Enumerated(EnumType.STRING)
    private StatusEnum status;



    private Long orderid;

}
