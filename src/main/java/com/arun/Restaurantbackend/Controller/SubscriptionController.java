package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.UserSubscribeRequest;
import com.arun.Restaurantbackend.Entity.Subscription;
import com.arun.Restaurantbackend.Service.SubscriptionService;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {



    private final SubscriptionService subscriptionService;




    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody  UserSubscribeRequest user) {

        try {
            Subscription sub = subscriptionService.processCheckout(user.getIdempotencyKey(), user.getUserId(), user.getPlanId());
            return ResponseEntity.ok(sub);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("DUPLICATE_REQUEST")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Request already processing. Please wait.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/cancelsubscription/{id}")
    public ResponseEntity<?> cancelorder(@PathVariable Long id){
        subscriptionService.cancelUserSubscription(id);
        return ResponseEntity.ok().build();
    }













}
