package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.annotation.EnableKafka;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GroupOtpTrack {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "groupCart_id")
    GroupCart groupCart;

    String otp;

    Long userid;

    boolean success=false;

}
