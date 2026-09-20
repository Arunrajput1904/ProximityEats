package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.AccessDeniedException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.RestaurantRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {



    private final RestaurantRepo restaurantRepo;
private final ModelMapper mapper;
private final Validationhandler validationhandler;
private final RestaurantService restaurantService;
private final AdminService adminService;
    @PreAuthorize("hasRole('MANAGER')")
    public List<RestaurantDto> findallrestaurantofmanager(Long id) {
        List<Restaurant> restaurantList=restaurantRepo.findBymanagerprofile(id);
        if(restaurantList.isEmpty()){
            throw new ResourceNoFoundException("Not any restaurant ");
        }
        return restaurantList.stream().map(item->mapper.map(item,RestaurantDto.class) ).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public RestaurantDto findbyname(String name) {
        User user=validationhandler.finduser();
        Restaurant restaurant=restaurantRepo.findByName(name).orElseThrow(()-> new ResourceNoFoundException("No restaurant of this name"));


        if(!restaurant.getManagerProfile().getId().equals(user.getId())){
            throw new AccessDeniedException("Restaurant id is not match with user id");
        }

        return mapper.map(restaurant,RestaurantDto.class);
    }


    @PreAuthorize("hasRole('MANAGER')")
    public RestaurantDto findbyid(Long id) {
        User user=validationhandler.finduser();
        Restaurant restaurant=restaurantRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("No restaurant of this name"));
        if(!restaurant.getManagerProfile().getId().equals(user.getId())){
            throw new AccessDeniedException("Restaurant id is not match with user id");
        }
        return mapper.map(restaurant,RestaurantDto.class);
    }


    @PreAuthorize("hasRole('MANAGER')")
    public RestaurantDto asignrestranunttomanager(Restaurant restaurant, Long id) {
        return adminService.asignrestranunttomanager(restaurant,id);
    }
}
