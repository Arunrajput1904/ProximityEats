package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey,Long> {
}
