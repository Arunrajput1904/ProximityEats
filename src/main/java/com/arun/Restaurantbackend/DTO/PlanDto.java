package com.arun.Restaurantbackend.DTO;


import jakarta.persistence.Id;
import lombok.Data;

@Data
public class PlanDto {
    private String planId; // e.g., "GOLD_3_MONTH", "GOLD_ANNUAL"
    private String name;   // e.g., "Zomato Gold 3 Months"
    private double price;  // e.g., 299.00
    private int durationInMonths; // e.g., 3 or 12
}