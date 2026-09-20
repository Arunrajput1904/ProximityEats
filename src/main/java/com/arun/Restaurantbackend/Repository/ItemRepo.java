package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<Item,Long> {
}

