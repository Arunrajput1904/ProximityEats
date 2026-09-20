package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.ManagerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerProfileRepo extends JpaRepository<ManagerProfile,Long> {


    @Query("select e from ManagerProfile e where e.user.email=:email")
    Optional<ManagerProfile> findByUseremail(@Param("email") String email);


    @Query("select e from ManagerProfile e where e.user.id=:id")
    Optional<ManagerProfile> findByUserid(@Param("id") Long id);
}
