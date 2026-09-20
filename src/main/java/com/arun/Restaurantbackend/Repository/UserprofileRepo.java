package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Userprofile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserprofileRepo extends JpaRepository<Userprofile,Long> {

    @Query("select e from Userprofile e where e.userid=:id")
    Optional<Userprofile> findByUserid(@Param("id") Long id);


    @Query("select e from Userprofile e where e.userid=:id")
    Optional<List<Userprofile>> findByuserid(@Param("id") Long userid);
}
