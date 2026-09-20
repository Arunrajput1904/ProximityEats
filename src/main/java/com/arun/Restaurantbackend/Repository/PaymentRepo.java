package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Payment;
import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Long> {

    @Query("select e from Payment e where e.orderRefId=:orderid and e.status=:status ")
    Payment findbyorderRefIdAndstatus(@Param("orderid") Long orderid,@Param("status") PaymentEnum paymentEnum);

    
}
