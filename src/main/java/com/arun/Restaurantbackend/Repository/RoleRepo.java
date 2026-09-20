package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role>  findByType(String admin);
}
