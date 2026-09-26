package com.arun.Restaurantbackend.Config;

import com.arun.Restaurantbackend.Entity.Role;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Entity.Wallet;
import com.arun.Restaurantbackend.Repository.RoleRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Repository.WalletRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepo userRepository;
    private final WalletRepo walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

               Optional<User>userOptional=userRepository.findByEmail("arunrajput161777@gmail.com");

               if(userOptional.isPresent()) {
                   return;
               }

         User admin = new User();
            admin.setName("Arun");
            admin.setEmail("arunrajput16177@gmail.com");
            admin.setProfileComplete(true);
            admin.setPassword(passwordEncoder.encode("Arun@2004"));
admin.setCancelApproveTime(0l);
admin.setPhoneNumber("9882534804");
            Role role=roleRepo.findByType("ADMIN").get();

            admin.setRole(role);

            Wallet adminWallet = new Wallet();
            adminWallet.setBalance(0.0);


            admin.setWallet(adminWallet);
            adminWallet.setUser(admin);

            userRepository.save(admin);

            System.out.println("Admin credentials and wallet successfully initialized.");
        }
    }
