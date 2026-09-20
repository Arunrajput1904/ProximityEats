package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//import java.lang.ScopedValue;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String username);

    @Query("select e from User e Join fetch e.wallet where e.id=:id")
    Optional<User> findByid(@Param("id") Long id);

}
