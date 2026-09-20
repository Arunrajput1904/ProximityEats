package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WalletRepo extends JpaRepository<Wallet,Long> {
}
