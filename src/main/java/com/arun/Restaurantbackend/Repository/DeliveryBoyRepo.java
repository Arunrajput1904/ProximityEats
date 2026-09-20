package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.DeliveryBoy;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryBoyRepo extends JpaRepository<DeliveryBoy,Long> {
    @Query("select e from DeliveryBoy e where e.user.id=:id")
    Optional<DeliveryBoy> findByUserid(@Param("id") Long id);

    @Query("select e from DeliveryBoy e join fetch e.user where e.orderid=:id")
    Optional<DeliveryBoy> findByorderid(@Param("id") Long id);


    @Query("select e from DeliveryBoy e where e.status=:statusEnum")
    List<DeliveryBoy> findByStatus(@Param("statusEnum") StatusEnum statusEnum);
}
