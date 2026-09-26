package com.arun.Restaurantbackend.Utilis;

public enum SubscriptionStatus {
    ACTIVE, CANCELLING, EXPIRED, PAST_DUE;

    public final boolean equals(SubscriptionStatus other) {
        return this == other;
    }
}
