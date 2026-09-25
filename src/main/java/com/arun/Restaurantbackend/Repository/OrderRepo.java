package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {

    @Query("select e from Order e where e.restaurant.id =:id")
    List<Order> findallbyrest(@Param("id") Long id);
    @Query("select e from Order e where e.user.id =:id")
    List<Order> findallbyuser(Long id);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select e from Order e left join fetch e.itemList where e.id=:id and e.status=:status ")
    Optional<Order> findByIdAndstatus(@Param("id") Long orderid,@Param("status") OrderEnum orderEnum);


    @Query("select e from Order e  left join fetch e.itemList where e.status=:status")
    List<Order> findallbystatus(@Param("status") OrderEnum orderEnum);


    @Query("select e from Order e where e.id=:id")
    Optional<Order> findByIdmethod(@Param("id") Long id);

    @Query("select e from Order e left join fetch e.itemList a Left join fetch e.restaurant k where e.status=:orderEnum  ")
    List<Order> findByStatus(@Param("orderEnum") OrderEnum orderEnum);


    @Query("SELECT o FROM Order o WHERE o.status = 'PAYMENT_PENDING' AND o.lastpaymentTime < :cutoffTime")
    List<Order> findExpiredPendingOrders(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT o FROM Order o WHERE o.status NOT IN ( 'PAYMENT_PENDING')")
    List<Order> findActiveOrdersForEvaluation();
}



