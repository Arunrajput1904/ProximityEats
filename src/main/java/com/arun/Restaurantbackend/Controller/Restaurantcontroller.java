package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.ItemDto;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Utilis.ItemAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant")
public class Restaurantcontroller {

    private final RestaurantService restaurantService;

    @GetMapping("/all/active")
    ResponseEntity<List<RestaurantDto>> getallactive() {
        List<RestaurantDto> list = restaurantService.getallbystatus("ACTIVE").orElseThrow(() -> new ResourceNoFoundException(" NO actibe resourece"));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/all/inactive")
    ResponseEntity<List<RestaurantDto>> getAllinactive() {
        List<RestaurantDto> list = restaurantService.getallbystatus("PENDING").orElseThrow(() -> new ResourceNoFoundException("No Pending restaurant"));
        return ResponseEntity.ok(list);
    }


    @GetMapping("/search/{id}")
    ResponseEntity<RestaurantDto> getByname(@PathVariable Long id) {

        RestaurantDto restaurantDto = restaurantService.findbyid(id);
        return ResponseEntity.ok(restaurantDto);
    }

    @GetMapping("/getrestitem/{id}")
    ResponseEntity<List<ItemDto>> getByRestId(@PathVariable Long id)
    {
        List<ItemDto>list=restaurantService.getAllmenuItem(id);
        return ResponseEntity.ok(list);
    }


    @PatchMapping("/changestatustohide/{id}")
    ResponseEntity<?> changestatusofitem(@PathVariable Long id){
        restaurantService.changeStatus(id, ItemAction.HIDE);
        return ResponseEntity.ok("Status is changed successfully");
    }

    @PatchMapping("/changestatustoavaliable/{id}")
    ResponseEntity<?> changestatusofitemm(@PathVariable Long id){
        restaurantService.changeStatus(id, ItemAction.AVAILABLE);
        return ResponseEntity.ok("Status is changed successfully");
    }








}
