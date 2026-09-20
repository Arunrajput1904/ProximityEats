package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
