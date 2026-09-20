package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.DTO.ProcessedWebhook;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProcessWebhookRepo extends JpaRepository<ProcessedWebhook,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(" select e from ProcessedWebhook e where e.eventId=:eventId")
    Optional<ProcessedWebhook> findByEventIdForUpdate(Long eventId);
}
