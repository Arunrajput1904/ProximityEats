package com.arun.Restaurantbackend.Utilis;

public enum OrderType {
    NORMAL,GROUP,BUNDLE;


    public final boolean equals(OrderType other) {
        return this == other;
    }
}
