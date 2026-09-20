package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.Entity.GroupCart;
import com.arun.Restaurantbackend.Entity.GroupPay;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerBrokerConnection {
    private final GroupPayRepo groupPayRepo;
    private final GroupCartRepo groupCartRepo;
    private final PaymentRepo paymentRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    void giveDataToClient(List<GroupPay>list, GroupCart groupCart){
        System.out.println(list+"  ,qewnhfllllllllllllllllllllllllllllllqx fbhsdv" +
                "fsfbwjehfbjrsefbvsbhbvhsrdbhbvhjbsd");
      list.stream()
              .forEach(x-> {
                  Map<String, Object> payload = Map.of(
                          "eventType", "TRIGGER_RAZORPAY_PAYMENT",
                          "cartId", x.getGroupid(),
                          "userPayments", x);


    String destination = "/topic/user/" + x.getUserid()+ "/cart";
    simpMessagingTemplate.convertAndSend(destination,(Object) payload);
                                });



    }


    @Transactional
    public void pushtheOrder(GroupCart groupCart) {

        GroupCart groupCart1=groupCartRepo.findById(groupCart.getId()).orElse(null);

        groupCart.setStatus(GroupCartStatus.OPENED);
        Map<String,Object>payload=Map.of(
                "eventType","TRIGGER_GROUP_CART",
                "cartId",groupCart.getId(),
                "groupCart",groupCart
        );
//        payload.put("timestamp", System.currentTimeMillis()); // Set current epoch millis
//        payload.put("expiresAt", System.currentTimeMillis() + (60 * 1000)); // 1 min expiration

        String destination = "/topic/society/" +groupCart.getSocietyId()+ "/cartinfo";
        simpMessagingTemplate.convertAndSend(destination,(Object)payload);
    }



}
