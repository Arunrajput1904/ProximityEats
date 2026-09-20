package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Cart;
import com.arun.Restaurantbackend.Entity.Cartitem;
import com.arun.Restaurantbackend.Utilis.CartEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart,Long> {

    @Query("Select e from Cart e where e.userId=:id")
    Optional<Cart> findByUserId(@Param("id") Long id);

    @Query("select e from Cart c JOIN c.list e where c.userId=:id")
    Optional<List<Cartitem>> findByitem(@Param("id") Long id);

    @Query("select e from Cart e where e.userId=:id and e.status=:cartenum ")
    Optional<Cart> findByUSerIdAndStatus(@Param("id") Long id,@Param("cartenum") CartEnum cartEnum);

    @Query("select e from Cartitem e where e.cart.id=:id")
    Optional<List<Cartitem>> findBycartid(@Param("id") Long id);
}
