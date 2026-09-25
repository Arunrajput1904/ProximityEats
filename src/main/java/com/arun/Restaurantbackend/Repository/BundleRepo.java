package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Bundle;
import com.arun.Restaurantbackend.Utilis.BundleStatus;
import lombok.Lombok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.LinkOption;
import java.util.List;
import java.util.Optional;

public interface BundleRepo extends JpaRepository<Bundle, Long> {

    @Query("select e from Bundle e where e.status=:bundleStatus")
    List<Bundle> findByStatus(@Param("bundleStatus") BundleStatus bundleStatus);


    @Query("select e from Bundle e where e.id=:id and e.status=:bundleStatus")
    Optional<Bundle> findByIdAndStatus(@Param("id") Long id, @Param("bundleStatus") BundleStatus bundleStatus);

    @Query("select e from Bundle e where e.status=:bundleStatus And e.deliveryBoy.id=:id")
     Optional<Bundle> findByDeliveryBoyidAndStatus(@Param("id") Long id,@Param("bundleStatus") BundleStatus bundleStatus);

    @Query("select e from Bundle e left join e.orderBundles o left  join  o.order or where or.id=:id ")
    Optional<Bundle> findOrderById(@Param("id") Long id);



}
