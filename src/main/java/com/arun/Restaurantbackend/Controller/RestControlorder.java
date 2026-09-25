package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Service.RestaurantService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "11. Order Status Apis", description = "Give Restuarant Manager to change status of order")
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


    {

        log.info(validationhandler.finduser()+"                                  uuuuuuuuuuuuuuuuuuuuuuuuuuuUser");

        OrderDto orderDto= restaurantService.madeorderstaaatuschange(Rest,orderid, OrderEnum.PREPARED,OrderEnum.PREPARING);
        return ResponseEntity.ok(orderDto);
    }
}
