package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}