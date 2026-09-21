package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.OrderRepo;
import com.arun.Restaurantbackend.Service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
@Tag(name = "7. Payment  Apis", description = "Give Payment full control")
public class PaymentController {
    @Value("${RAZOR_PAY_KEY}")
    String razorKeyId;


    private final OrderRepo orderRepo;
    private  final PaymentService paymentService;
    private final RazorpayClient razorpayClient;


    @PostMapping("/order/{orderId}")
    public void madepayement(@PathVariable Long orderId, HttpServletResponse httpServletResponse){
        paymentService.makepaymentoforder(orderId,httpServletResponse);
    }




    @PostMapping("/wallet/{orderId}")
    public ResponseEntity<OrderDto> WalletPay(@PathVariable Long orderId){

        OrderDto order=paymentService.makeorderByWallet(orderId);

        return new  ResponseEntity<>(order, HttpStatus.CREATED);
    }






    @PostMapping("/razorpaycreate/{orderId}")
    public ResponseEntity<?> getOrderIdRazorpay(@PathVariable Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() ->
                new ResourceNoFoundException("Order id is not found"));
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", order.getTotalGrant().doubleValue() * 100);
            jsonObject.put("currency", "INR");
            jsonObject.put("receipt", "txn_" + System.currentTimeMillis());
            com.razorpay.Order order1 = razorpayClient.orders.create(jsonObject);

            return ResponseEntity.ok(
                    Map.of("keyId", razorKeyId,
                            "orderId", order1.get("id"),
                                "orderIdd",order.getId(),
                            "amount", order.getTotalGrant(),
                            "name", order.getUser().getName(),
                            "email", order.getUser().getUsername(),
                            "contact", order.getUser().getPhoneNumber()
                    )
            );
        } catch (RazorpayException e) {
            throw new BadRequestException("Payment is not done");
        }
    }








}
