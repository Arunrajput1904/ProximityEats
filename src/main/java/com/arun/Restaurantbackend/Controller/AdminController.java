package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.AdminResponseDto;
import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Service.AdminService;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@Tag(name = "7. Admin Apis", description = "Give Access/remove/validate to restaurant ")
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
private final RestaurantService restaurantService;
private final Validationhandler validationhandler;

    @PostMapping("/asignrestauranttouser/{id}")
    public ResponseEntity<AdminResponseDto> asignrestauranttouser(@RequestBody @Valid Restaurant restaurant, @PathVariable Long id){
        RestaurantDto restaurantDto=adminService.asignrestranunttomanager(restaurant,id);
        AdminResponseDto adminResponseDto=AdminResponseDto.builder().
                adminName(validationhandler.finduser().getName()).restaurant(new ArrayList<>(List.of(restaurantDto))).build();
        return ResponseEntity.ok(adminResponseDto);
    }


    @PatchMapping("/set/restaurant/{id}/status/{name}")
    ResponseEntity<AdminResponseDto> asiginStatus(@PathVariable Long id, @PathVariable String name)  {
        RestaurantDto restaurantDto=restaurantService.setstatus(id,name);
        AdminResponseDto adminResponseDto=AdminResponseDto.builder().adminName(validationhandler.finduser().getName()).restaurant(new ArrayList<>(List.of(restaurantDto))).build();
        return ResponseEntity.ok(adminResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/searchrestaurant/{id}")
    ResponseEntity<AdminResponseDto> getsearchbyid(@PathVariable Long id){
        RestaurantDto restaurantDto=restaurantService.findbyid(id);
        AdminResponseDto adminResponseDto=AdminResponseDto.builder().adminName(validationhandler.finduser().getName()).restaurant(new ArrayList<>(List.of(restaurantDto))).build();
       return ResponseEntity.ok(adminResponseDto);
    }
    @GetMapping("/all/activerestaurant")
    ResponseEntity<AdminResponseDto> getallactive(){
        List<RestaurantDto>list=restaurantService.getallbystatus("ACTIVE").orElseThrow(()->new ResourceNoFoundException(" NO actibe resourece"));
        AdminResponseDto adminResponseDto=AdminResponseDto.builder().adminName(validationhandler.finduser().getName()).restaurant(list).build();
        return ResponseEntity.ok(adminResponseDto);
    }
    @GetMapping("/all/inactiverestaurant")
    ResponseEntity<AdminResponseDto>getAllinactive(){
        List<RestaurantDto>list=restaurantService.getallbystatus("PENDING").orElseThrow(()-> new ResourceNoFoundException("No Pending restaurant"));
        AdminResponseDto adminResponseDto=AdminResponseDto.builder().adminName(validationhandler.finduser().getName()).restaurant(list).build();
        return ResponseEntity.ok(adminResponseDto);
    }
}
