package com.arun.Restaurantbackend.Controller;

import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.DTO.Orderaddress;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Service.OrderService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "6. Menu Apis", description = "Give Access/remove/validate to restaurant ")
public class OrderController{

    private final OrderService orderService;
    private final Validationhandler validationhandler;

    @CachePut(cacheNames = "orderitem",key = "#result.body.id")
    @PostMapping("/madeorder")
    public ResponseEntity<OrderDto> makeorder(@RequestBody  @Valid  Orderaddress orderaddress){
        log.info("orderid............"+orderaddress.getAddressId());
      OrderDto orderDto=orderService.makeorder(validationhandler.finduser(),orderaddress);
      return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/restorder/restuarant/{restid}")
    public  ResponseEntity<List<OrderDto>>  getallorderofRestrastraunt(@PathVariable Long restid){
           List<OrderDto>list=orderService.findallbyrest(restid).orElseThrow(()-> new ResourceNoFoundException("Not found "));
           return  ResponseEntity.ok(list);

    }

    @GetMapping("/restorder/item/{userid}")
    public  ResponseEntity<List<OrderDto>>  getallorderofuser(@PathVariable Long userid){
        List<OrderDto>list=orderService.findallbyuser(userid).orElseThrow(()-> new ResourceNoFoundException("Not found "));
        return  ResponseEntity.ok(list);

    }

    @PatchMapping("/usercancel/{id}")
    public  ResponseEntity<OrderDto>  cancelorderbyuser(@PathVariable Long id){
        OrderDto orderDto=orderService.cancelorder(id);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/restaurantcancel/{id}")
    public  ResponseEntity<OrderDto>  cancelorderbyrest(@PathVariable Long id){
        OrderDto orderDto=orderService.cancelorderbyrest(id);
        return ResponseEntity.ok(orderDto);
    }

    @PatchMapping("/deliveryboycancel/{id}")
    public ResponseEntity<OrderDto>  cancelorderbydelivery(@PathVariable Long id){
        OrderDto orderDto=orderService.cancelybydelivery(id);
        return ResponseEntity.ok(orderDto);
    }

}
