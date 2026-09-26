package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.RoleEnum;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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



    @Scheduled(cron = "0/25 * * * * *")
    @Transactional
    public void orderSettlementTask() {

        for (Order order : orderRepo.findByStatus(OrderEnum.PAYMENT_DONE)) {

            if (order.getOrderType()==(OrderType.BUNDLE)) {
                Bundle bundle = bundleRepo.findOrderById(order.getId()).orElse(null);
                if (bundle != null) {


                    bundleRepo.delete(bundle);
                }
                log.info(bundle+"  deleted ");
            }
            orderRepo.delete(order);

        }
    }




    @Transactional
    @Scheduled(cron = "0/10 * * * * *")
    void ordersettlement() {

        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Order> expiredOrders = orderRepo.findExpiredPendingOrders(tenMinutesAgo);

        for (Order order : expiredOrders) {
            if (order.getOrderType().equals(OrderType.BUNDLE)) {
                Bundle bundle = bundleRepo.findOrderById(order.getId()).orElse(null);
                if (bundle != null) {


                    bundleRepo.delete(bundle);
                }
            }
            else {
                order.setStatus(OrderEnum.PAYMENT_DONE);
            }
        }

        orderRepo.saveAll(expiredOrders);

        List<Order> ordersToEvaluate = orderRepo.findActiveOrdersForEvaluation();
        ordersToEvaluate.forEach(this::evaluateorder);



        log.info("Order settlement task completed successfully."+" "+orderRepo.findAll());


    }



  @Transactional
  public   void evaluateorder(Order order){

log.info("Order is send  for evaluation.....");
        User Normaluser=order.getUser();

        Normaluser=userRepo.findByid(Normaluser.getId()).orElseThrow(()-> new ResourceNoFoundException("not found"));

        Restaurant restaurant=restaurantRepo.findByrestId(order.getRestaurant().getId()).orElseThrow(()-> new ResourceNoFoundException("Not found"));

        ManagerProfile manager = restaurant.getManagerProfile();


        User admin = userRepo.findByEmail(AdminEmial).orElseThrow(() -> new ResourceNoFoundException("Not found"));
if(order.getStatus().equals(OrderEnum.CONFIRMED) && order.getLastpaymentTime().isAfter(LocalDateTime.now())){
    return;
}
        if (order.getStatus().equals(OrderEnum.PAYMENT_PENDING) || order.getStatus().equals(OrderEnum.PAYMENT_DONE)) {
            if (order.getOrderType().equals(OrderType.BUNDLE)) {
                Bundle bundle = bundleRepo.findOrderById(order.getId()).orElse(null);
                if (bundle != null) {
log.info(bundle+"  deleted ");

                    bundleRepo.delete(bundle);
                }
            }else{
                orderRepo.delete(order);
            }
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
            order.setStatus(OrderEnum.PAYMENT_DONE);
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
                    order.setStatus(OrderEnum.PAYMENT_DONE);
                } else {
                    order.setStatus(OrderEnum.PAYMENT_DONE);

                    userRepo.delete(Normaluser);
                }
            } else if (order.getCancel().getReason().equals(RoleEnum.MANAGER)) {
                User manageruser = manager.getUser();
                if (manageruser.getCancelApproveTime() < Cancelationlimit*8) {
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
                    order.setStatus(OrderEnum.PAYMENT_DONE);

                } else {
                    order.setStatus(OrderEnum.PAYMENT_DONE);


                    userRepo.delete(manageruser);
                }
            } else {
                DeliveryBoy deliveryBoy = null;
                if(order.getOrderType().equals(OrderType.BUNDLE)){
                    Bundle bundle = bundleRepo.findOrderById(order.getId()).orElseThrow(() -> new ResourceNoFoundException("Resource is not found"));

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
                log.info(userwallet.getBalance()+"  ....................................................................");
                order.setStatus(OrderEnum.PAYMENT_DONE);

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
                    order.setStatus(OrderEnum.PAYMENT_DONE);
                    restaurant.setCancelApproveTime(restaurant.getCancelApproveTime()+1l);
                }
                else{
                    order.setStatus(OrderEnum.PAYMENT_DONE);
                    restaurant.setStatus(String.valueOf(StatusEnum.INACTIVE));
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

                order.setStatus(OrderEnum.PAYMENT_DONE);
            }
            else if (order.getStatus().equals(OrderEnum.OUT_FOR_DELIVERY)){
                DeliveryBoy deliveryBoy = null;
                if(order.getOrderType()==OrderType.BUNDLE){
                    Bundle bundle = bundleRepo.findOrderById(order.getId()).orElseThrow(() -> new ResourceNoFoundException("Resource is not found"));
                    deliveryBoy= bundle.getDeliveryBoy();

                    log.info(bundle+"        Delivery System ................??????????????????");

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

                    order.setStatus(OrderEnum.PAYMENT_DONE);

                } else {
                    order.setStatus(OrderEnum.PAYMENT_DONE);

                    userRepo.delete(userprofile);
                }
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
