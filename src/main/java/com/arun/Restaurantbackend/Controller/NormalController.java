package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.GroupCartRepo;
import com.arun.Restaurantbackend.Repository.GroupPayRepo;
import com.arun.Restaurantbackend.Repository.OrderRepo;
import com.arun.Restaurantbackend.Service.GroupCartService;
import com.arun.Restaurantbackend.Service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NormalController {
    private final GroupCartRepo groupCartRepo;
    private final GroupCartService groupCartService;
    private final OrderRepo orderRepo;
private final PaymentService paymentService;
private final GroupPayRepo groupPayRepo;


    @GetMapping("/api/pay/payment/{orderId}")
    public String makePayment(@PathVariable Long orderId, Model model){
        model.addAttribute("orderId",orderId);
        return "payment";
    }

    @PostMapping("/api/pay/paymentonline/{orderid}")
    public String redirectTOPaymentMethod(@PathVariable Long orderid, Model model){
        model.addAttribute("orderId",orderid);
        Order order=orderRepo.findById(orderid).orElseThrow(()->
                new ResourceNoFoundException("Order id is not found"));
        model.addAttribute("orderPrice",order.getTotalGrant());
        return "OnlinePay";
    }


    @GetMapping("api/pay/onlinepay/{orderId}/{paymentId}")
    public String OnlinePay(@PathVariable Long orderId,@PathVariable String paymentId,Model model){
        log.info("Online Pay executed ...");
        OrderDto order=paymentService.makeorderByOnline(orderId);
        model.addAttribute("orderId",orderId);
        model.addAttribute("paymentId",paymentId);
        log.info("Online Pay executed ...");
        return "paymentsuccess";
    }


    @GetMapping("/loginsuccess")
    public void loginSuccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("LoginSuccess.html");
    }




}
