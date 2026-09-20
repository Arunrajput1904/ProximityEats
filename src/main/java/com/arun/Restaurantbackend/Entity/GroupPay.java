package com.arun.Restaurantbackend.Entity;

import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class GroupPay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    Long userid;


    Integer totalPrice;


    Long groupid;

    @Column(name = "razorPay_generated_id", nullable = true)
    String razorPay_generated_id;


    String secretKey;


    @Enumerated(EnumType.STRING)
    PaymentEnum payStatus;

    public GroupPay(Long id, Double price) {
        this.userid=id;
        this.totalPrice=((Number) price).intValue();
    }


    public void addmoney(Integer Price){
        this.totalPrice+=Price;
    }
}
