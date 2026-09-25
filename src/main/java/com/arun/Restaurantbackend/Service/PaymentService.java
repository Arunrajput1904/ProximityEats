package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.Estimatetimekm;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.CartEnum;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final Validationhandler validationhandler;
    private final WalletRepo walletRepo;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UserprofileRepo userprofileRepo;
    private final Mailservice mailservice;
    private final DELIVERYASSIGNSERVICE deliveryassignservice;
    @Value("${admin.email}")
    String adminEmail;
    private final UserRepo userRepo;

    public final ModelMapper mapper;


    @PreAuthorize("hasRole('USER')")

    public void makepaymentoforder(Long orderid, HttpServletResponse httpServletResponse) {
        Payment payment = new Payment();
        payment.setStatus(PaymentEnum.INITIATED);
        payment.setOrderRefId(orderid);
        List<Payment> list = paymentRepo.findAll();
        payment.setCounter((long) list.size());
        Order order;
        try {

            User user = validationhandler.finduser();
            Payment payment1 = paymentRepo.findbyorderRefIdAndstatus(orderid, PaymentEnum.PAID);
            if (payment1 != null) {
                throw new IllegalPaymentException("Order payment is already done");
            }
            order = orderRepo.findById(orderid).orElseThrow(() -> new ResourceNoFoundException("not found order"));
            String restaurantmanageremail = order.getRestaurant().getManagerProfile().getUser().getEmail();
            User adminuser = userRepo.findByEmail(adminEmail).orElseThrow(() -> new ResourceNoFoundException(" " +
                    "Admin is not registered"));
            if (!(user.getId().equals(order.getUser().getId()))) {
                throw new InvalidRequestException("Invalid order id");
            }

            Userprofile userprofile = userprofileRepo.findByUserid(user.getId()).orElseThrow(() ->
                    new ResourceNoFoundException("not found userprofile"));


            httpServletResponse.sendRedirect("/api/pay/payment/"+orderid);

        } catch (IllegalPaymentException exception) {
            payment.setStatus(PaymentEnum.FAILED);
            paymentRepo.save(payment);
            throw new IllegalPaymentException(exception.getMessage());
        } catch (ResourceNoFoundException exception) {
            payment.setStatus(PaymentEnum.FAILED);
            paymentRepo.save(payment);
            throw new ResourceNoFoundException(exception.getMessage());
        } catch (RuntimeException exception) {
            payment.setStatus(PaymentEnum.FAILED);
            paymentRepo.save(payment);
            throw new RuntimeException(exception.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public OrderDto makeorderByWallet(Long orderid) {

        User user = validationhandler.finduser();
        Order order = orderRepo.findById(orderid).orElseThrow(() -> new ResourceNoFoundException("not found order"));

        User adminuser = userRepo.findByEmail(adminEmail).orElseThrow(() -> new ResourceNoFoundException(" " +
                "Admin is not registered"));

        Payment payment1 = paymentRepo.findbyorderRefIdAndstatus(orderid, PaymentEnum.PAID);
        if (payment1 != null) {
            throw new IllegalPaymentException("Order payment is already done");
        }

        Wallet wallet = user.getWallet();

        Double m1 = wallet.getBalance();
        Double m2 = Double.valueOf(String.valueOf(order.getTotalGrant()));

        if (m1 < m2) {
            throw new IllegalPaymentException("Insufficient money");
        }
//
//        if (order.getLastpaymentTime().isBefore(LocalDateTime.now())) {
//            throw new IllegalPaymentException("payment session expired , try again");
//        }
        Cart cart = cartRepo.findByUSerIdAndStatus(user.getId(), CartEnum.ACTIVE).orElseThrow(() -> new ResourceNoFoundException("Not found cart"));
        synchronized (this) {
            Double Admincurrentbalance = adminuser.getWallet().getBalance();
            Double userwalletmoney = wallet.getBalance();
            userwalletmoney -= Double.valueOf(String.valueOf(order.getTotalGrant()));
            Admincurrentbalance += Double.valueOf(String.valueOf(order.getTotalGrant()));
            adminuser.getWallet().setBalance(Admincurrentbalance);
            user.getWallet().setBalance(userwalletmoney);
        }
        return orderSuccess(order,user,cart);

    }


    public OrderDto makeorderByOnline(Long orderid) {
        System.out.println("Online method start");
        User user = validationhandler.finduser();
        Order order = orderRepo.findById(orderid).orElseThrow(() -> new ResourceNoFoundException("not found order"));
        User adminuser = userRepo.findByEmail(adminEmail).orElseThrow(() -> new ResourceNoFoundException(" " +
                "Admin is not registered"));
        System.out.println("order done1");
        Cart cart = cartRepo.findByUSerIdAndStatus(user.getId(), CartEnum.ACTIVE).orElseThrow(() -> new ResourceNoFoundException("Not found cart"));
        synchronized (this) {
            Double Admincurrentbalance = adminuser.getWallet().getBalance();
            Admincurrentbalance += Double.valueOf(String.valueOf(order.getTotalGrant()));
            adminuser.getWallet().setBalance(Admincurrentbalance);
        }
        System.out.println("order done");
        return orderSuccess(order,user,cart);
    }




    OrderDto orderSuccess(Order order, User user, Cart cart) {
        log.info("Processing order success for user ID: " + user.getId());

        if (cart == null) {
            cart = cartRepo.findByUSerIdAndStatus(user.getId(), CartEnum.ACTIVE)
                    .orElseThrow(() -> new ResourceNoFoundException("Cart is not found"));
        }

        System.out.println(" order success");

        Payment payment = new Payment();
        long paymentCount = paymentRepo.count();
        payment.setCounter(paymentCount);

        order.setStatus(OrderEnum.CONFIRMED);
        Userprofile userprofile = userprofileRepo.findByUserid(user.getId())
                .orElseThrow(() -> new ResourceNoFoundException("not found userprofile"));
        order.setAddress(userprofile);

        Estimatetimekm estimatetimekm = deliveryassignservice
                .findestimatedistanceandtime(userprofile.getSocietyName(), order.getRestaurant().getTown());

        order.setEstimatekm(estimatetimekm.getEstkm());
        order.setEstimatedeliverytime(estimatetimekm.getEstimatetime());

        long minutes = Duration.between(LocalDateTime.now(), estimatetimekm.getEstimatetime()).toMinutes();
        order.setEstimatetime(minutes + " min");
        order.setUsertorestloc(estimatetimekm.getStringBuilder());

        order.setOrderAcceptTime(LocalDateTime.now().plusMinutes(2));
        order.setLastUpdateTime(LocalDateTime.now());

        // 2. Soft-delete the cart (DO NOT use cartRepo.delete(cart) here)
        cart.setStatus(CartEnum.INACTIVE);
        cartRepo.save(cart);


        Cart cart1 = new Cart();
        cart1.setUserId(user.getId());
        cart1.setStatus(CartEnum.ACTIVE);
        cartRepo.save(cart1);


        userRepo.save(user);
        Order order1 = orderRepo.save(order);

        payment.setOrderRefId(order1.getId());
        payment.setStatus(PaymentEnum.PAID);
        paymentRepo.save(payment);


        mailservice.sendmailofOrder(user.getEmail(), order);
        mailservice.sendmailofOrder(order.getRestaurant().getEmail(), order);

        return mapper.map(order1, OrderDto.class);
    }
}







