package com.arun.Restaurantbackend.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "user_service")
public class Wallet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ToString.Exclude
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    User user;



    Double balance;

    @Version
    Long version=0L;


   public void addmoney(Double m){
        if(m<0){
            return;
        }
        balance+=m;
    }

}
