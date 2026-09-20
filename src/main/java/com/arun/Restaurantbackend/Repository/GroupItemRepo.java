package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.GroupItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupItemRepo extends JpaRepository<GroupItem,Long> {
}
