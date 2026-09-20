package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deliveryboyemail {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @NotNull
    Long userid;


    @NotEmpty
    String currentpassword;

    @NotNull
    Long count;
//    String  password;

}
