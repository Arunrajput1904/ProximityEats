package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestControlorder {
private final Validationhandler validationhandler;
private final RestaurantService restaurantService;

@PatchMapping("/change-status/{Restid}/prepareorder/{orderid}")
     ResponseEntity<OrderDto>  makeorderstatusprepare(@PathVariable Long Restid, @PathVariable Long orderid)
    {
        OrderDto orderDto=restaurantService.madeorderstaaatuschange(Restid,orderid, OrderEnum.PREPARING,OrderEnum.CONFIRMED);
        return ResponseEntity.ok(orderDto);
    }
    @PatchMapping("/change-status/{Rest}/preparedorder/{orderid}")
    ResponseEntity<OrderDto>  makeorderstatusprepared(@PathVariable Long Rest,@PathVariable Long orderid)
    {OrderDto orderDto= restaurantService.madeorderstaaatuschange(Rest,orderid, OrderEnum.PREPARED,OrderEnum.PREPARING);
        return ResponseEntity.ok(orderDto);
    }
}
