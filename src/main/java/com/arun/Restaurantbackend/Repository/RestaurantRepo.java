package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepo extends JpaRepository<Restaurant,Long> {

//    Optional<List<Restaurant>> findAllByStatus();

    @Query("select e from Restaurant e where e.status=:status")
    List<Restaurant>getallbystatus(@Param("status") String status);

    @Query("select e from Restaurant e where e.name=:name")
    Optional<Restaurant> findByName(@Param("name") String name);

    @Query("select e from Restaurant e where e.email=:email")
    Optional<Restaurant> findByemail(@Param("email") String email);


    @Query("select e from Restaurant e where e.status=:status")
    Optional<List<Restaurant>> getallbystatuss(@Param("status")String active, Pageable pageable);


    @Query("Select e from Restaurant e Join fetch e.managerProfile m  Join fetch m.user u " +
            " Join fetch u.wallet w where e.id=:id")
    Optional<Restaurant>findByrestId(@Param("id") Long id);

    @Query("select e from Restaurant e Join fetch e.managerProfile m where m.id=:id")
    List<Restaurant> findBymanagerprofile(@Param("id") Long id);

    @Query("select e from Restaurant e where e.name=:name ")
    Optional<List<Restaurant>> findByNames(@Param("name") String name);
}



