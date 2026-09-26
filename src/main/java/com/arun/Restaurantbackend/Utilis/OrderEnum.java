package com.arun.Restaurantbackend.Utilis;

public enum OrderEnum {
    PAYMENT_PENDING,
    CONFIRMED,
    PREPARING,
    PREPARED,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    REFUND,
    PROCESSING_REFUND,
    PAYMENT_DONE, EXPIRED_TIME;


    public final boolean equals(OrderEnum other) {
        return this == other;
    }
}
