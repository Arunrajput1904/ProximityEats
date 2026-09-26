package com.arun.Restaurantbackend.Utilis;

public enum GroupCartStatus {
    ACTIVE, LOCKED, CANCELLED,COMPLETED,OPENED;

    public final boolean equals(GroupCartStatus other) {
        return this == other;
    }
}
