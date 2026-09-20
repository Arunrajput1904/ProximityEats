package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
//@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public  class Stop{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
            Long id;
    Long bundleId;
    int index;
    public int value;

    public Stop(int i, int i1) {
        this.index = i;
        this.value=i1;
    }
}