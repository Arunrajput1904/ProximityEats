package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final Validationhandler validationhandler;
    private final WalletRepo walletRepo;
    private final UserRepo userRepo;


    public Wallet add(Long amount) {
        log.info("amount......................" + amount);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount is Invalid");
        }

        User user = validationhandler.finduser();

        Wallet wallet = user.getWallet();

        wallet.setBalance(wallet.getBalance() +amount);

        System.out.println();
        userRepo.save(user);

        return wallet;

    }

    public Wallet getbalance(){
        return validationhandler.finduser().getWallet();
    }


}