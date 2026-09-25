package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


//@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public  class Stop{


    Long bundleId;
    int index;
     int value;
    String placeName;
    public Stop(String placeName,int i, int i1) {
        this.index = i;
        this.value=i1;
        this.placeName=placeName;
    }


}