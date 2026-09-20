package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyKey {
    @Id
    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;
    private LocalDateTime lockedAt;
}
