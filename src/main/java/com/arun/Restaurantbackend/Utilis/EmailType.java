package com.arun.Restaurantbackend.Utilis;

public enum EmailType {

    OTP,
    CONFIRMATION,
    DELIVERY_OTP,
    SUBSCRIPTION_CREATED,
    SUBSCRIPTION_RENEW,
    SUBSCRIPTION_REMAINDER;

    public final boolean equals(EmailType other) {
        return this == other;
    }
}
