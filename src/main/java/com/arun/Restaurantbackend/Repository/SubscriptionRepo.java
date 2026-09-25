package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Subscription;
import com.arun.Restaurantbackend.Utilis.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription,Long> {

    @Query(" select e from  Subscription e where e.gatewaySubId=:gatewaySubId")
    Optional<Subscription> findByGatewaySubId(String gatewaySubId);

    @Query("select e from Subscription e where e.status=:subscriptionStatus AND e.nextBillingDate=:now ")
    List<Subscription> findByNextBillingAndstatus(LocalDate now, SubscriptionStatus subscriptionStatus);


    @Query("select e from Subscription e where e.userId=:userid And e.planId=:Subid")
    Optional<Subscription> findByUserIdAndSubId(Long userid, Long Subid);
}
