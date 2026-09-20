package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.ItemDto;
import com.arun.Restaurantbackend.DTO.MenuDto;
import com.arun.Restaurantbackend.Entity.Item;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.AccessDeniedException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.ItemRepo;
import com.arun.Restaurantbackend.Repository.RestaurantRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final RestaurantRepo restaurantRepo;
private final ModelMapper mapper;
    private final ItemRepo repo;
private final Validationhandler validationhandler;

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public MenuDto addmenuitem(Item menuItem, Long id) {
        Restaurant restaurant=restaurantRepo.findById(id).orElseThrow(()->
                new ResourceNoFoundException("No found rest"));
      User user=  validationhandler.finduser();
      if(!restaurant.getManagerProfile().getUser().getUsername().equals(user.getUsername())){
          throw new AccessDeniedException("Not valid Access to restaurant");
      }
        menuItem.setMenu(restaurant.getMenu());
        restaurant.getMenu().getItemList().add(menuItem);
        return mapper.map(restaurant.getMenu(),MenuDto.class);
    }


    @Transactional
    @PreAuthorize("@validationhandler.validaterest(#id)")
    public MenuDto removemenuitem(Item menuItem, Long id) {
        Restaurant restaurant=restaurantRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("No found rest"));
        Item item=restaurant.getMenu().getItemList().stream().
                filter(Item->Item.getName().equals(menuItem.getName())).findFirst()
                .orElseThrow(()->new ResourceNoFoundException("Menuitem is not found"));
        restaurant.getMenu().getItemList().remove(item);
        item.setMenu(null);
        return mapper.map(restaurant.getMenu(),MenuDto.class);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public ItemDto updateprice(Long id, Long price) {

        Item item=repo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Item is not found"));

        item.setPrice(Double.valueOf(price));


        return mapper.map(item,ItemDto.class);


    }
}
