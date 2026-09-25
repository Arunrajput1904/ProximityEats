package com.arun.Restaurantbackend.Controller;

import com.arun.Restaurantbackend.DTO.BundleDto;
import com.arun.Restaurantbackend.DTO.BundleRequest;
import com.arun.Restaurantbackend.DTO.BundleResponse;
import com.arun.Restaurantbackend.Entity.Bundle;
import com.arun.Restaurantbackend.Service.BundleService;
import com.arun.Restaurantbackend.Utilis.BundleStatus;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/bundle")
@RequiredArgsConstructor
@Tag(name = "13. BundleOrder Apis", description = "Bundle delete,update,validate,acceptBydeliveryboy, Many more")
public class BundleController {

    private final BundleService bundleService;

    @GetMapping("/livebundle")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<List<BundleResponse>> getAllBundles(){
        List<BundleResponse>bundleList=bundleService.findAllBundleItem();
        return ResponseEntity.ok(bundleList);
    }

    @PatchMapping("/acceptbundle/{id}")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<BundleDto> acceptBundle(@PathVariable  Long id){
         BundleDto bundleDtos=bundleService.makethebundleaccept(id);

         return ResponseEntity.ok(bundleDtos);
    }

    @PatchMapping("/cancelbundle/{id}")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id){
        bundleService.cancelBundle(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deliverdbundle/{id}")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public  ResponseEntity<?> deliverdundle(@PathVariable Long id){
        bundleService.deliveredBundle(id);

        return ResponseEntity.ok().build();
    }



    @PatchMapping("/deliveredOrder/{id}")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<?> deliveredOrder(@PathVariable Long id){
        bundleService.orderstatus(id, OrderEnum.CONFIRMED);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notdeliveredOrder/{id}")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<?>  notdeliverorder(@PathVariable Long id){
        bundleService.orderstatus(id,OrderEnum.PROCESSING_REFUND);
        return ResponseEntity.ok().build();
    }





    @PatchMapping("/prepared/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BundleDto> preparedBundle(@PathVariable Long id){
        BundleDto bundleDto=bundleService.preparedBundle(id);
        return  ResponseEntity.ok(bundleDto);
    }

    @PatchMapping("/cancelbundlee/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> cancelOrders(@PathVariable Long id){
       bundleService.canceloverallBundle(id);
        return ResponseEntity.ok().build();
    }



}
