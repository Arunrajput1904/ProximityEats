package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderitemRepo extends JpaRepository<OrderItem,Long> {
}
