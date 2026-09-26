package com.arun.Restaurantbackend.Utilis;


public enum StatusEnum {
   AVAILABLE,
    BUSY,
    UNAVAILABLE,
    SUSPENDED,
    ACTIVE,
 INACTIVE, PENDING;

 public final boolean equals(StatusEnum other) {
  return this == other;
 }
}
