package com.arun.Restaurantbackend.Entity;


import com.arun.Restaurantbackend.Utilis.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CancelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long userid;
    @NotNull
    Long orderid;
    @NotNull
    @Enumerated(EnumType.STRING)
    RoleEnum reason;

}
