package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.OrderBundle;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderBundleRepo extends JpaRepository<OrderBundle,Long> {
}
