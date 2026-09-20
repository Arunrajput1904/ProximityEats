package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepo extends JpaRepository<Menu,Long> {
}
