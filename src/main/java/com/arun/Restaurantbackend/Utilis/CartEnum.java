package com.arun.Restaurantbackend.Utilis;

public enum CartEnum {
    ADD,REMOVE,ACTIVE,INACTIVE;

    public final boolean equals(CartEnum other) {
        return this == other;
    }
}
