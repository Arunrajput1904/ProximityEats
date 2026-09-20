package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepo extends JpaRepository<Permission,Long> {
}
