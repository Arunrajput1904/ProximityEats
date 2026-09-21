package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.Deliveryboydashboard;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.DTO.Otp;
import com.arun.Restaurantbackend.Entity.DeliveryBoy;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.DeliveryBoyRepo;
import com.arun.Restaurantbackend.Service.DeliveryService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "12. Delivery Boy Apis ", description = "Give access/accept/cancel order ")
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final DeliveryBoyRepo deliveryBoyRepo;
private final Validationhandler validationhandler;


    @GetMapping("/orderlive")
    public ResponseEntity<Deliveryboydashboard>getall(){
      User user=validationhandler.finduser();
        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid(user.getId()).orElseThrow(()->
                new ResourceNoFoundException("not found "));
        List<OrderDto> orderDtoList =deliveryService.getallorderservice().orElseThrow(()->
                new ResourceNoFoundException(" no found order, try again"));
        Deliveryboydashboard deliveryboydashboard=Deliveryboydashboard.builder().deliveryBoyname(user.getName()
        ).list(orderDtoList).boystatus(deliveryBoy.getStatus().toString()).build();
       return ResponseEntity.ok(deliveryboydashboard);
    }

    @GetMapping("/ordersearch/{id}")
    public ResponseEntity<Deliveryboydashboard>getbynameoforder(@PathVariable Long id){
        User user=validationhandler.finduser();
        OrderDto orderDto=deliveryService.getorderbyid(id);
        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid(user.getId()).orElseThrow(()-> new ResourceNoFoundException("not found "));
        Deliveryboydashboard deliveryboydashboard=Deliveryboydashboard.builder().deliveryBoyname(user.getName()).list(new ArrayList<>(List.of(orderDto))).boystatus(deliveryBoy.getStatus().toString()).build();
        return ResponseEntity.ok(deliveryboydashboard);
    }

    @GetMapping("/orderstatus/{id}")
    public  ResponseEntity<OrderDto> getorder(@PathVariable Long id){
        OrderDto orderDto=deliveryService.getorderbyid(id);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/orderaccept/{id}")
    public  ResponseEntity<OrderDto> getorderaccept(@PathVariable Long id){
        OrderDto orderDto=deliveryService.getorderacceptbyid(id);
        return ResponseEntity.ok(orderDto);
    }
    @PatchMapping("/orderpickup/{id}")
    public  ResponseEntity<OrderDto> getorderpickup(@PathVariable Long id){
        OrderDto orderDto=deliveryService.takepickup(id);
        return ResponseEntity.ok(orderDto);
    }
    @PatchMapping("/orderdelivered/{id}")
    public  ResponseEntity<OrderDto> getorderdelivered(@RequestBody Otp otp, @PathVariable Long id){
        OrderDto orderDto=deliveryService.takedelivered(otp.getOtp(), id);
        return ResponseEntity.ok(orderDto);
    }


}
