package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.CartDto;
import com.arun.Restaurantbackend.DTO.CartitemDto;
import com.arun.Restaurantbackend.DTO.Cartitemintilization;
import com.arun.Restaurantbackend.DTO.UserCartresponse;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Service.CartService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import com.arun.Restaurantbackend.Utilis.CartEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "5. User Cart Apis", description = "item add/remove/update in cart")
public class CartController {


    private final CartService cartService;
    private final Validationhandler validationhandler;


    @PostMapping("/add")
    public ResponseEntity<UserCartresponse> additemtocart(@RequestBody  @Valid Cartitemintilization cartdata){
User user=validationhandler.finduser();
       CartDto cart= cartService.additemtocart(cartdata.getRestid(),cartdata.getItemid());

       AtomicReference<Double> totalmoney= new AtomicReference<>(0.0);
       if(!cart.getList().isEmpty()){
           cart.getList().stream().forEach(cartitemDto ->{
               totalmoney.updateAndGet(v -> v + cartitemDto.getPrice() * cartitemDto.getQuantity());
           });
       }

        UserCartresponse userCartresponse=UserCartresponse.builder().username(user.getName()).cart(cart).total(totalmoney.get()).build();
       return ResponseEntity.ok(userCartresponse);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<UserCartresponse> removetemtocart(@RequestBody  @Valid Cartitemintilization cartdata){
        User user=validationhandler.finduser();
        CartDto cart= cartService.removeitemtocart(cartdata.getRestid(),cartdata.getItemid());
        AtomicReference<Double> totalmoney= new AtomicReference<>(0.0);
        if(!cart.getList().isEmpty()){
            cart.getList().stream().forEach(cartitemDto ->{
                totalmoney.updateAndGet(v -> v + cartitemDto.getPrice() * cartitemDto.getQuantity());
            });
        }

        UserCartresponse userCartresponse=UserCartresponse.builder().username(user.getName()).cart(cart).total(totalmoney.get()).build();
        return ResponseEntity.ok(userCartresponse);
    }

    @GetMapping("/allitem")
    public ResponseEntity<List<CartitemDto>> getall(){
        List<CartitemDto>list=cartService.getall();
    return     ResponseEntity.ok(list);
    }

    @PatchMapping("/update/inc")
    public ResponseEntity<UserCartresponse> updateemtoaddcart(@RequestBody  @Valid  Cartitemintilization catdata){
        User user=validationhandler.finduser();
        CartDto cart= cartService.changequantity(catdata.getRestid(),catdata.getItemid(), CartEnum.ADD);
        AtomicReference<Double> totalmoney= new AtomicReference<>(0.0);
        if(!cart.getList().isEmpty()){
            cart.getList().stream().forEach(cartitemDto ->{
                totalmoney.updateAndGet(v -> v + cartitemDto.getPrice() * cartitemDto.getQuantity());
            });
        }

        UserCartresponse userCartresponse=UserCartresponse.builder().username(user.getName()).cart(cart).total(totalmoney.get()).build();
        return ResponseEntity.ok(userCartresponse);
    }
    @PatchMapping("/update/dec")
    public ResponseEntity<UserCartresponse> updateemtoremovecart(@RequestBody   @Valid Cartitemintilization catdata){
User user=validationhandler.finduser();
        CartDto cart= cartService.changequantity(catdata.getRestid(),catdata.getItemid(),CartEnum.REMOVE);

        AtomicReference<Double> totalmoney= new AtomicReference<>(0.0);
        if(!cart.getList().isEmpty()){
            cart.getList().stream().forEach(cartitemDto ->{
                totalmoney.updateAndGet(v -> v + cartitemDto.getPrice() * cartitemDto.getQuantity());
            });
        }

        UserCartresponse userCartresponse=UserCartresponse.builder().username(user.getName()).cart(cart).total(totalmoney.get()).build();
        return ResponseEntity.ok(userCartresponse);
    }


    
}
