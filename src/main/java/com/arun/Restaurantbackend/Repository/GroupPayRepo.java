package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.GroupPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupPayRepo extends JpaRepository<GroupPay,Long> {

    @Query("select e from GroupPay e where e.userid=:id AND e.groupid=:groupid")
    Optional<GroupPay> findByUserIdAndGroupId(@Param("groupid") Long groupCart,@Param("id") Long userid);

    @Query("select e from GroupPay e where e.groupid=:groupid")
    List<GroupPay> findGroupId(@Param("groupid") Long id);

//    @Query("select e from  GroupPay e where e.groupid=:id")
//    List<GroupPay> findByGroupId(Long id);
}
