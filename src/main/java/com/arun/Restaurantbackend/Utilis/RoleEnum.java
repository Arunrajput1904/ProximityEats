package com.arun.Restaurantbackend.Utilis;

public enum RoleEnum {
    ADMIN,MANAGER,USER,DELIVERY_BOY;

    public final boolean equals(RoleEnum other) {
        return this == other;
    }
}
