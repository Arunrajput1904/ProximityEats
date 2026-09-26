package com.arun.Restaurantbackend.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.SessionAttributes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public  class StopDto{
    Long id;
    int index;
    public int value;

    public StopDto(int i, int i1) {
        this.index = i;
        this.value=i1;
    }
}
