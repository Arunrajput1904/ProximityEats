package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.Managerresponse;
import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Service.ManagerService;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
@Tag(name = "8. Manager Apis", description = "Give access to read/update/add restaurant ")
public class ManagerController {
    private final ManagerService managerService;
private final RestaurantService restaurantService;
    private final Validationhandler validationhandler;

    @GetMapping("/getallrestaurant")
    public ResponseEntity<Managerresponse> findall(){
        User user=validationhandler.finduser();
              List<RestaurantDto> list=managerService.findallrestaurantofmanager(user.getId());
              Managerresponse managerresponse=Managerresponse.builder().email(user.getEmail()).name(user.getName()).restaurantDtoList(list).build();
              return ResponseEntity.ok(managerresponse);
    }

    @GetMapping("/getrestaurantbyid/{id}")
    public ResponseEntity<Managerresponse> findbyid(@PathVariable Long id){
        User user=validationhandler.finduser();
        RestaurantDto restaurantDto =managerService.findbyid(id);
        Managerresponse managerresponse=Managerresponse.builder().email(user.getEmail()).name(user.getName()).restaurantDtoList(new ArrayList<>(List.of(restaurantDto))).build();
        return ResponseEntity.ok(managerresponse);
    }
    @GetMapping("/getrestaurantbyname/{name}")
    public ResponseEntity<Managerresponse> findbyname(@PathVariable String name){
        User user=validationhandler.finduser();
        RestaurantDto restaurantDto =managerService.findbyname(name);
        Managerresponse managerresponse=Managerresponse.builder().email(user.getEmail()).name(user.getName()).restaurantDtoList(new ArrayList<>(List.of(restaurantDto))).build();
        return ResponseEntity.ok(managerresponse);
    }

    @PostMapping("/assignrestaurant")
    public ResponseEntity<Managerresponse> assignrestaurant(@RequestBody   @Valid  Restaurant restaurant){
        User user=validationhandler.finduser();
        RestaurantDto restaurantDto =managerService.asignrestranunttomanager(restaurant,user.getId());
        Managerresponse managerresponse=Managerresponse.builder().email(user.getEmail()).name(user.getName()).restaurantDtoList(new ArrayList<>(List.of(restaurantDto))).build();
        return ResponseEntity.ok(managerresponse);
    }



}
