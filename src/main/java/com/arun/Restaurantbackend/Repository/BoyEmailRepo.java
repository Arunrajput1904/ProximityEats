package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Deliveryboyemail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoyEmailRepo extends JpaRepository<Deliveryboyemail,Long> {
    @Modifying
    @Transactional
    @Query("delete  from Deliveryboyemail e where e.userid=:id")
    void deletepervious(@Param("id") Long id);

    @Query("select e from Deliveryboyemail e where e.userid=:id")
    Optional<Deliveryboyemail> findByUserid(@Param("id") Long id);
}
