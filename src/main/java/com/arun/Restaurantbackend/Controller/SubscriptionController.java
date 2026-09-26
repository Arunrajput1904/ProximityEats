package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.UserSubscribeRequest;
import com.arun.Restaurantbackend.Entity.Subscription;
import com.arun.Restaurantbackend.Service.SubscriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Tag(name = "15. Subscription Apis")
public class SubscriptionController {



    private final SubscriptionService subscriptionService;




    @PostMapping("/subscribe")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> subscribe(@RequestBody  UserSubscribeRequest user) {
        try {
            Subscription sub = subscriptionService.processCheckout(user.getIdempotencyKey(),user.getPlanId());
            return ResponseEntity.ok(sub);
        }
     catch (RuntimeException e) {
        if ("DUPLICATE_REQUEST".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Request already processing. Please wait.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
    }

    @PatchMapping("/cancelsubscription/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> cancelorder(@PathVariable Long id){
        subscriptionService.cancelUserSubscription(id);
        return ResponseEntity.ok("Subscription Cancel Successfully");
    }













}
