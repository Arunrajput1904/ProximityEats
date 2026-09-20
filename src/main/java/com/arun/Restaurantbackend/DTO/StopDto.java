package com.arun.Restaurantbackend.DTO;


public  class StopDto{
    Long id;
    int index;
    public int value;

    public StopDto(int i, int i1) {
        this.index = i;
        this.value=i1;
    }
}
