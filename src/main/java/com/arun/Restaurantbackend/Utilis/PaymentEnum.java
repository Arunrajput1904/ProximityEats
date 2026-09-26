package com.arun.Restaurantbackend.Utilis;

public enum PaymentEnum {
    PAID,INITIATED,FAILED;

    public final boolean equals(PaymentEnum other) {
        return this == other;
    }
}
