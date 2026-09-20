package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class RefundService {


    private static final Logger log = LoggerFactory.getLogger(RefundService.class);

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final RestaurantRepo restaurantRepo;
    private final DeliveryBoyRepo deliveryBoyRepo;
    private final long Cancelationlimit = 10;

    @Value("${admin.email}")
    String AdminEmial;

    private final GroupPayRepo groupPayRepo;

    private final GroupCartRepo groupCartRepo;
    private final BundleRepo bundleRepo;


    @Transactional
    @Scheduled(cron = "0 0 7 * * *")
    void ordersettlement() {
        List<Order> orderList = orderRepo.findAll();

        if(orderList!=null){
            orderList.stream().forEach(order->  evaluateorder(order));
        }
    }



  @Transactional
  public   void evaluateorder(Order order){


        User Normaluser=order.getUser();

        Normaluser=userRepo.findByid(Normaluser.getId()).orElseThrow(()-> new ResourceNoFoundException("not found"));

        Restaurant restaurant=restaurantRepo.findByrestId(order.getRestaurant().getId()).orElseThrow(()-> new ResourceNoFoundException("Not found"));

        ManagerProfile manager = restaurant.getManagerProfile();


        User admin = userRepo.findByEmail(AdminEmial).orElseThrow(() -> new ResourceNoFoundException("Not found"));

        if (order.getStatus().equals(OrderEnum.PAYMENT_PENDING)) {
            orderRepo.delete(order);
            return;
        }

        if (order.getEstimatedeliverytime().isAfter(LocalDateTime.now())) {
            return;
        }
        if (order.getStatus().equals(OrderEnum.DELIVERED)) {

             DeliveryBoy deliveryBoy = deliveryBoyRepo.findByorderid(order.getId()).orElseThrow(() -> new ResourceNoFoundException("Delivery boy is not found"));
             Wallet restaurantwallet = manager.getUser().getWallet();
             Wallet adminwallet = admin.getWallet();
             Double orderprice = Double.valueOf(String.valueOf(order.getOrderItemCharge()));
             adminwallet.setBalance(adminwallet.getBalance() - orderprice);
             restaurantwallet.setBalance(restaurantwallet.getBalance() + orderprice);
            if(!order.getOrderType().equals(OrderType.BUNDLE)) {
                User deliveryboyuser = deliveryBoy.getUser();
                Wallet deliveryboywallet = deliveryboyuser.getWallet();
                Double deliveryboyfees = Double.valueOf(String.valueOf(order.getDeliveryFees())) * 0.7;
                adminwallet.setBalance(adminwallet.getBalance() - deliveryboyfees);
                deliveryboywallet.setBalance(deliveryboywallet.getBalance() + deliveryboyfees);
            }
             orderRepo.delete(order);
//         }
            return;
        }

        if (order.getStatus().equals(OrderEnum.PROCESSING_REFUND) && order.getCancel() != null) {
            if (order.getCancel().getReason().equals(RoleEnum.USER)) {
                if (Normaluser.getCancelApproveTime() < Cancelationlimit) {

                    Wallet adminwallet = admin.getWallet();
                    Wallet userwallet = Normaluser.getWallet();


                    if(order.getOrderType().equals(OrderType.GROUP)){
                        grouppayrefund(adminwallet,order);
                    }
                    else{
                        Double orderprice1 = Double.valueOf(String.valueOf(order.getTotalGrant()));
                        adminwallet.setBalance(adminwallet.getBalance() - orderprice1);
                        userwallet.setBalance(userwallet.getBalance() + orderprice1);
                    }
                    Normaluser.setCancelApproveTime(Normaluser.getCancelApproveTime() + 1);

                    orderRepo.delete(order);
                } else {
                    orderRepo.delete(order);
                    userRepo.delete(Normaluser);
                }
            } else if (order.getCancel().getReason().equals(RoleEnum.MANAGER)) {
                User manageruser = manager.getUser();
                if (manageruser.getCancelApproveTime() < Cancelationlimit*3) {
                    Wallet adminwallet = admin.getWallet();
                    Wallet userwallet = Normaluser.getWallet();
                    if(order.getOrderType().equals(OrderType.GROUP)){
                        grouppayrefund(adminwallet,order);
                    }
                    else {
                        Double orderprice1 = Double.valueOf(String.valueOf(order.getTotalGrant()));
                        adminwallet.setBalance(adminwallet.getBalance() - orderprice1);
                        userwallet.setBalance(userwallet.getBalance() + orderprice1);

                    }
                    manageruser.setCancelApproveTime(manageruser.getCancelApproveTime() + 1);
                    orderRepo.delete(order);
                } else {
                    orderRepo.delete(order);
                    userRepo.delete(manageruser);
                }
            } else {
                DeliveryBoy deliveryBoy = null;
                if(order.getOrderType().equals(OrderType.BUNDLE)){
                    Bundle bundle = bundleRepo.findOrderByid(order.getId()).orElseThrow(() -> new ResourceNoFoundException("Resource is not found"));

                    deliveryBoy= bundle.getDeliveryBoy();
                }
                else{
                    deliveryBoy = deliveryBoyRepo.findByorderid(order.getId()).orElseThrow(()
                            -> new ResourceNoFoundException("Delivery boy is not found"));
                }



                User manageruser = manager.getUser();


                User userprofile = deliveryBoy.getUser();

                if (userprofile.getCancelApproveTime() < Cancelationlimit*3) {
                    Wallet restaurantwallet = manageruser.getWallet();
                    Wallet adminwallet = admin.getWallet();
                    Double orderprice = Double.valueOf(String.valueOf(order.getOrderItemCharge()));
                    adminwallet.setBalance(adminwallet.getBalance() - orderprice);
                    restaurantwallet.setBalance(restaurantwallet.getBalance() + orderprice);
                    userprofile.setCancelApproveTime(manageruser.getCancelApproveTime() + 1);
                    Wallet userwallet = Normaluser.getWallet();
                    if(order.getOrderType().equals(OrderType.GROUP)){
                        grouppayrefund(adminwallet,order);
                    }
                    else {
                        Double orderprice1 = Double.valueOf(String.valueOf(order.getTotalGrant()));
                        adminwallet.setBalance(adminwallet.getBalance() - orderprice1);
                        userwallet.setBalance(userwallet.getBalance() + orderprice1);
                    }
                } else {
                    userRepo.delete(userprofile);
                }

            }
        } else if (order.getEstimatedeliverytime().isBefore(LocalDateTime.now())
                || order.getOrderAcceptTime().isBefore(LocalDateTime.now()) && order.getStatus().ordinal() > 3) {
            if (order.getStatus().equals(OrderEnum.CONFIRMED)) {

                Wallet userwallet=Normaluser.getWallet();
                Wallet adminwallet=admin.getWallet();
                if(order.getOrderType().equals(OrderType.GROUP)){
                    grouppayrefund(adminwallet,order);
                }
                else {
                    Double orderprice = Double.valueOf(String.valueOf(order.getTotalGrant()));
                    adminwallet.setBalance(adminwallet.getBalance() - orderprice);
                    userwallet.setBalance(userwallet.getBalance() + orderprice);
                }
                order.setStatus(OrderEnum.PAYMENT_PENDING);
                orderRepo.delete(order);
            }
            else if(order.getStatus().equals(OrderEnum.PREPARING)){
                if(restaurant.getCancelApproveTime()<=Cancelationlimit){

                      Wallet adminwallet=admin.getWallet();
                    Wallet userwallet=Normaluser.getWallet();

                    Double orderprice1=Double.valueOf(String.valueOf(order.getTotalGrant()));

                    if(order.getOrderType().equals(OrderType.GROUP)){
                        grouppayrefund(adminwallet,order);
                    }
                    else {
                        adminwallet.setBalance(adminwallet.getBalance() - orderprice1);
                        userwallet.setBalance(userwallet.getBalance() + orderprice1);
                    }
                    order.setStatus(OrderEnum.PAYMENT_PENDING);
                    restaurant.setCancelApproveTime(restaurant.getCancelApproveTime()+1l);
                }
                else{
                    orderRepo.delete(order);
                    restaurantRepo.delete(restaurant);
                }
            }
            else if(order.getStatus().equals(OrderEnum.PREPARED)){
                Wallet restaurantwallet=restaurant.getManagerProfile().getUser().getWallet();
                Wallet adminwallet=admin.getWallet();
                Double orderprice=Double.valueOf(String.valueOf(order.getOrderItemCharge()));
                adminwallet.setBalance(adminwallet.getBalance()-orderprice);
                restaurantwallet.setBalance(restaurantwallet.getBalance()+orderprice);


                Wallet userwallet=Normaluser.getWallet();
                if(order.getOrderType().equals(OrderType.GROUP)){
                    grouppayrefund(adminwallet,order);
                }
                else {
                    Double orderprice1 = Double.valueOf(String.valueOf(order.getTotalGrant()));
                    adminwallet.setBalance(adminwallet.getBalance() - orderprice1);
                    userwallet.setBalance(userwallet.getBalance() + orderprice1);
                }

                order.setStatus(OrderEnum.PAYMENT_PENDING);
            }


        }
    }

    private void grouppayrefund(Wallet adminwallet, Order order) {
        GroupCart groupCart=groupCartRepo.findByOrderId(order.getId()).orElseThrow(()-> new ResourceNoFoundException("No groupCart find"));
        List<GroupPay>groupPays=groupPayRepo.findGroupId(groupCart.getId())   ;
        List<User>userlist = groupPays.stream()
                .map(x -> x.getUserid())
                .map(x -> userRepo.findById(x))
                .filter(x -> x.get() != null)
                .map(x -> x.get())
                .toList();

        for (User x : userlist) {
            Wallet wallet = x.getWallet();
            for(GroupPay groupPay : groupPays){
                if(groupPay.getUserid().equals(x.getId())) {
                    adminwallet.setBalance(adminwallet.getBalance() - groupPay.getTotalPrice());
                    wallet.setBalance(wallet.getBalance() + groupPay.getTotalPrice());
                    break;
                }
            }
        }
    }

}
