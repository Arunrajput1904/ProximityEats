package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.Entity.Wallet;
import com.arun.Restaurantbackend.Service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
@Tag(name = "4. Wallet Apis", description = "Give Access to user to add money in wallet ")
public class Walletcontroller {

    private final WalletService walletService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add/{amount}")
    public Wallet addmoney(@PathVariable  Long amount){
return         walletService.add(amount);
    }

    @GetMapping("/get")
    public  Wallet getmoney(){
        return walletService.getbalance();
    }
}
