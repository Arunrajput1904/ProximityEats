package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.Entity.Wallet;
import com.arun.Restaurantbackend.Service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class Walletcontroller {

    private final WalletService walletService;

    @PostMapping("/add/{amount}")
    public Wallet addmoney(@PathVariable  Long amount){
return         walletService.add(amount);
    }

    @GetMapping("/get")
    public  Wallet getmoney(){
        return walletService.getbalance();
    }
}
