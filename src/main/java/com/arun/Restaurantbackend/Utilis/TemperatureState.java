package com.arun.Restaurantbackend.Utilis;

public enum TemperatureState {

    HOT,COLD,AMBIENT;
    public final boolean equals(TemperatureState other) {
        return this == other;
    }
}
