package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.CartDto;
import com.arun.Restaurantbackend.DTO.CartitemDto;
import com.arun.Restaurantbackend.Entity.Cart;
import com.arun.Restaurantbackend.Entity.Cartitem;
import com.arun.Restaurantbackend.Entity.Item;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.IllegalAccessException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.CartRepo;
import com.arun.Restaurantbackend.Repository.ItemRepo;
import com.arun.Restaurantbackend.Repository.cartItemRepo;
import com.arun.Restaurantbackend.Utilis.CartEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

//import static com.arun.Restaurantbackend.Service.RefundService.log;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepo cartRepo;
private final Validationhandler validationhandler;
private final ModelMapper mapper;
private final ItemRepo itemRepo;
private final cartItemRepo cartItemRepo;

@Transactional
@PreAuthorize("hasRole('USER')")
public CartDto additemtocart(Long restid, Long itemid) {

    User user=validationhandler.finduser();

    Cart cart=cartRepo.findByUSerIdAndStatus(user.getId(),CartEnum.ACTIVE).orElseThrow(()-> new ResourceNoFoundException(" no cart found"));


    if(cart.getRestaurantid() == null){
            cart.setRestaurantid(restid);
        }
        else if(!cart.getRestaurantid().equals(restid)){
            cart.getList().clear();
            cart.setRestaurantid(restid);
        }
 Item item=itemRepo.findById(itemid).orElseThrow(()-> new ResourceNoFoundException("Not found"));


        if(!(item.getMenu().getRestaurant().getId().equals(restid))){
            throw  new ResourceNoFoundException("Invalid restaurant item ");
        }


        Cartitem cartitem1=cart.getList().stream().filter(items->items.getItemid()
                .equals(item.getId())).findFirst().orElse(null);

        if(cartitem1==null){
            Cartitem cartitem=new Cartitem();
            cartitem.setCart(cart);
            cartitem.setName(item.getName());
            cartitem.setPrice(item.getPrice());
            cartitem.setQuantity(1);
            cartitem.setItemid(itemid);
            cart.getList().add(cartitem);
        }
        else {
            cartitem1.setQuantity(cartitem1.getQuantity() + 1);
        }
        cart.setLastUpdateTime(LocalDateTime.now());
        return mapper.map(cart,CartDto.class);

    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public CartDto removeitemtocart(Long restid, Long itemid) {

        User user=validationhandler.finduser();
        Cart cart=cartRepo.findByUserId(user.getId()).orElse(null);
        if(cart.getRestaurantid()==null){
            throw new IllegalStateException("Cart not associated with a restaurant");
        }
        else if(!cart.getRestaurantid().equals(restid)){
            throw  new IllegalAccessException("No valid restaurant id");
        }



        Item item=itemRepo.findById(itemid).orElseThrow(()-> new ResourceNoFoundException("Not found"));


        if(!(item.getMenu().getRestaurant().getId().equals(restid))){
            throw  new IllegalAccessException("Invalid restaurant id ");
        }
        Cartitem cartitem1=cart.getList().stream().filter(items->items.getItemid().equals(item.getId())).findFirst().orElse(null);

        if(cartitem1==null){
  throw  new ResourceNoFoundException("Not found item in cart");
        }
        else {
            cart.getList().remove(cartitem1);
        }
        cart.setLastUpdateTime(LocalDateTime.now());

        return mapper.map(cart,CartDto.class);

    }

    @PreAuthorize("hasRole('USER')")
    public List<CartitemDto> getall(){
        User user=validationhandler.finduser();
    Cart cart=cartRepo.findByUSerIdAndStatus(user.getId(),CartEnum.ACTIVE).orElseThrow(()-> new ResourceNoFoundException(" no cart found"));

        List<Cartitem> cartitems=cartRepo.findBycartid(cart.getId()).orElseThrow(()-> new ResourceNoFoundException("Not found"));

        return cartitems.stream().map(item->mapper.map(item,CartitemDto.class)).collect(Collectors.toList());



    }


//    public CartDto changequantity()

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public CartDto changequantity(Long restid, Long itemid, CartEnum cartEnum) {
    User user=validationhandler.finduser();
        Cart cart=cartRepo.findByUserId(user.getId()).orElse(null);
        if(cart.getRestaurantid()==null){
            throw  new IllegalStateException("No valid creenditals");
        }
        else if(!cart.getRestaurantid().equals(restid)){
            throw  new IllegalAccessException("No valid data");
        }
        Item item=itemRepo.findById(itemid).orElseThrow(()-> new ResourceNoFoundException("Not found"));
        if(!(item.getMenu().getRestaurant().getId().equals(restid))){
            throw  new ResourceNoFoundException("Invalid restaurant item ");
        }
        Cartitem cartitem1=cart.getList().stream().filter(items->items
                .getItemid().equals(item.getId())).findFirst().orElse(null);
        if(cartitem1==null){
            throw  new ResourceNoFoundException("Not found item in cart");
        }
        else {
            if(cartEnum==CartEnum.REMOVE){
                cartitem1.setQuantity(cartitem1.getQuantity() - 1);
                if(cartitem1.getQuantity()==0){
                    cart.getList().remove(cartitem1);
                }
            }
            else{
                cartitem1.setQuantity(cartitem1.getQuantity() + 1);
            }
        }
        cart.setLastUpdateTime(LocalDateTime.now());
        return mapper.map(cart,CartDto.class);
    }



}
