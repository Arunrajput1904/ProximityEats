package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.GroupOtpTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupOtpTrackRepo  extends JpaRepository<GroupOtpTrack,Long> {

    @Query("select e from GroupOtpTrack e where e.userid=:id ")
    Optional<GroupOtpTrack> findByUserid(Long userid);


    @Query("SELECT e FROM GroupOtpTrack e WHERE e.groupCart.id = :id")
    List<GroupOtpTrack> findbyGroupid(@Param("id") Long id);


}
