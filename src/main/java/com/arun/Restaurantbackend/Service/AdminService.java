package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Entity.ManagerProfile;
import com.arun.Restaurantbackend.Entity.Menu;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Exception.ResourceAlreadyExistsException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.ManagerProfileRepo;
import com.arun.Restaurantbackend.Repository.RestaurantRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {


    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;
    private final ManagerProfileRepo managerProfileRepo;
    private  final ModelMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public RestaurantDto asignrestranunttomanager(Restaurant restaurant, Long id) {
        ManagerProfile managerProfile=managerProfileRepo.findByUserid(id)
                .orElseThrow(()->new ResourceNoFoundException("ManagerProfile is not found ," +
                        " firstly please create profile"));

        String email=restaurant.getEmail();




      Restaurant restaurant1 = restaurantRepo.findByemail(email).orElse(null);
      log.info(restaurant1 + "................" + restaurant);
      if (restaurant1 != null) {
          throw new ResourceAlreadyExistsException("Already email exist in other restaurant  ");
      }
      Menu menu = restaurant.getMenu();
      menu.setRestaurant(restaurant);
      restaurant.getMenu().getItemList().stream().forEach(item -> item.setMenu(menu));
      restaurant.setManagerProfile(managerProfile);
        restaurantRepo.save(restaurant);
        return mapper.map(restaurant,RestaurantDto.class);
    }
}
