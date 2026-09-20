package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.SessionEntity;
import com.arun.Restaurantbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepo extends JpaRepository<SessionEntity,Long> {

    @Query("SELECT e FROM SessionEntity e WHERE e.user.id = :id")
    List<SessionEntity> findAllByuserId(Long id);

    Optional<SessionEntity> findByRefreshToken(String refreshtoken);

    void deleteByRefreshToken(String refreshToken);



    void deleteAllByUser(User user1);
}
