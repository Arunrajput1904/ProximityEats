package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Society {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @NotEmpty
    String societyName;

    @NotEmpty
    String city;

    @NotEmpty
    String pinCode;


    @Override
    public boolean equals(Object o){
        if(o==this) return true;

        if(o.getClass()!=this.getClass()){
            return false;
        }
        Society society = ((Society)o);
        return society.societyName.equals(this.societyName) && society.getPinCode().equals(this.getSocietyName());

    }

}
