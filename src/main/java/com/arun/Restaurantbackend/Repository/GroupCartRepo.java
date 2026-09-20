package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.GroupCart;
import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface GroupCartRepo extends JpaRepository<GroupCart,Long> {
    @Query("select e from GroupCart e where e.uniqueId=:uniqueId And e.status=:groupCartStatus")
    Optional<GroupCart> findByIdAndStatus(@Param("uniqueId") String uniqueId,
                                          @Param("groupCartStatus") GroupCartStatus groupCartStatus);
    @Query("select e from GroupCart e where e.id=:groupId And e.status=:groupCartStatus")
    Optional<GroupCart> findByGroupIdAndStatus(@Param("groupId") Long groupId,
                                               @Param("groupCartStatus") GroupCartStatus groupCartStatus);

    @Query("SELECT e FROM GroupCart e WHERE e.expiresAt <= :time AND e.status = :groupCartStatus")
    List<GroupCart> findByStatusAndExpiresAt(
            @Param("groupCartStatus") GroupCartStatus groupCartStatus,
            @Param("time") LocalDateTime now
    );

    @Query("select e from GroupCart e where e.paymentAt<=:time And e.status=:groupCartStatus")
    List<GroupCart> findbyStatusAndExpiresPayAt(@Param("groupCartStatus") GroupCartStatus groupCartStatus,
                                             @Param("time") LocalDateTime now);


    @Query("select e from GroupCart e where e.status=:groupStatus")
    List<GroupCart> findByStatus(@Param("groupStatus") GroupCartStatus groupCartStatus);


    @Query("select e from GroupCart e where e.orderId=:id")
    Optional<GroupCart> findByOrderId(Long id);
}
