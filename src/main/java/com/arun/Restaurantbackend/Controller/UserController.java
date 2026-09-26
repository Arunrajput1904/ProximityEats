package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.DemoRestaurant;
import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.DTO.UserResponse2;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Entity.Userprofile;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.UserprofileRepo;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Service.UserprofileService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "3. User  Apis", description = "Give Access to user to add address , find restaurant etc ")
public class UserController {



    private final RestaurantService restaurantService;
    private final   UserprofileRepo userprofileRepo;
    private final UserprofileService userprofileService;
    private  final Validationhandler validationhandler;
    private  final ModelMapper mapper;



    public String locationuser(){
        User user=validationhandler.finduser();
        Userprofile userprofile=userprofileRepo.findByUserid(user.getId()).orElseThrow(()-> new ResourceNoFoundException("Invalid creditials"));
        return userprofile.getCity();
    }




    @PreAuthorize("hasRole('USER')")
    @GetMapping("/searchRestuarant/{name}")
    public ResponseEntity<UserResponse2> findbyname(@PathVariable String name){
       User user=validationhandler.finduser();
        List<DemoRestaurant>list=restaurantService.findbynames(name);
        UserResponse2 userResponse2=UserResponse2.builder().name(user.getName()).email(user.getEmail()).restaurantDtoList(list).build();
        return ResponseEntity.ok(userResponse2);
    }



    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getallrestaurant")
    public  ResponseEntity<UserResponse2> findall(@RequestParam(defaultValue = "name") String SortBy, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "1")Integer size){
        User user=validationhandler.finduser();
        List<DemoRestaurant>list=restaurantService.findbystatus(SortBy,pageNumber,size, "ACTIVE");
        UserResponse2 userResponse2=UserResponse2.builder().name(user.getName()).email(user.getEmail()).restaurantDtoList(list).build();
        return ResponseEntity.ok(userResponse2);
    }



    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getrestaurant/{id}")
    public ResponseEntity<RestaurantDto> getbyid(@PathVariable Long id){
           RestaurantDto restaurantDto=restaurantService.findbyid(id);
           return ResponseEntity.ok(restaurantDto);
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addaddress")
    public  ResponseEntity<Userprofile> userprofileadd(@RequestBody @Valid Userprofile userprofile){
        Userprofile userprofile1=userprofileService.addaddress(userprofile);

        return ResponseEntity.ok(userprofile1);
    }
}
