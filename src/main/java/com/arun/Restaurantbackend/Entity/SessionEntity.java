package com.arun.Restaurantbackend.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Builder
@Setter
@Table(schema = "user_service")
public class SessionEntity {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    String refreshToken;

    @ManyToOne
    @JoinColumn(name = "User_session_id")
    User user;



    @CreationTimestamp
    LocalDateTime localDateTime;


}
