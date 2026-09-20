package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Handler.Mailsender;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class MakeGroupOrderService {
    private final EmailvalidationRepo emailvalidationRepo;
    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;
  private final Mailsender mailsender;
    private final GroupCartRepo groupCartRepo;
    private final GroupItemRepo groupItemRepo;
    @Value("${RAZOR_PAY_KEY}")
    String razorKeyId;
    private final RazorpayClient razorpayClient;

   private final DELIVERYASSIGNSERVICE deliveryassignservice;
    private final GroupPayRepo groupPayRepo;
    private final SocietyRepo societyRepo;
private final ServerBrokerConnection serverBrokerConnection;
    private final UserRepo userRepo;
private final PaymentService paymentService;
//private final TimerService timerService;


    @Transactional
    public void makeOrder(GroupCart cart) {

//        System.out.println(cart+".........................we................................................................................................");
        Long id=cart.getId();
      GroupCart groupCart = groupCartRepo.findByGroupIdAndStatus(id, GroupCartStatus.ACTIVE).
                orElseThrow(() -> new ResourceNoFoundException("GroupCart is not found"));
        makeFalseItemDelete(groupCart);
        makeFalseUserDelete(groupCart);
groupCart.setStatus(GroupCartStatus.LOCKED);
   makePaymentSplit(groupCart);
    }
    private void makePaymentSplit(GroupCart groupCart) {
        double deliveryfee=groupCart.getDeliveryFees();
        double splitFee=deliveryfee/groupCart.getParticipates().size();
        List<GroupPay>paylist=groupPayRepo.findGroupId(groupCart.getId());
        List<GroupItem>list=groupCart.getGroupItemList();
         list.stream().
        forEach(x->{
            for(GroupPay groupPay : paylist){
                if(groupPay.getUserid().equals(x.getAddedBy().getId())){
                    groupPay.addmoney((int) (x.getPrice()*x.getQuantity()));
                }
            }
        });
        for(GroupPay groupPay :  paylist){
            groupPay.addmoney((int) splitFee);
        }

//        System.out.println(groupPay);
        generatedRazorayPayId(paylist,groupCart);
    }

    private void generatedRazorayPayId(List<GroupPay> list1,GroupCart groupCart) {

        list1.stream().forEach(order->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", order.getTotalPrice() * 100);
            jsonObject.put("currency", "INR");
            jsonObject.put("receipt", "txn_" + System.currentTimeMillis());
            try {
                com.razorpay.Order order1 = razorpayClient.orders.create(jsonObject);
                 order.setRazorPay_generated_id(order1.get("id"));
                 order.setSecretKey(razorKeyId);
                 order.setPayStatus(PaymentEnum.INITIATED);
                 order.setGroupid(groupCart.getId());
            } catch (RazorpayException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(list1+"         grdsfdsfdddddddddddddddddddddddddddd");
     groupPayRepo.saveAll(list1);
        System.out.println(list1+".........................................................");
        serverBrokerConnection.giveDataToClient(list1,groupCart);

    }

    private void makeFalseUserDelete(GroupCart groupCart) {

        List<User>userList=new ArrayList<>();
        List<GroupPay>userPay=new ArrayList<>();
        groupCart.getParticipates().stream()
                .forEach(x-> {
                    AtomicBoolean flag= new AtomicBoolean(false);
                    groupCart.getGroupItemList()
                            .stream()
                            .forEach(xx->{
                                boolean ss=xx.getAddedBy().getId().equals(x.getId());
                                if(ss){
                                    flag.set(true);
                                }
                            });
                    if (!flag.get()){
                        Optional<GroupPay> byUserIdAndGroupId = groupPayRepo.findByUserIdAndGroupId(groupCart.getId(), x.getId());

                        if(byUserIdAndGroupId.isPresent()){
                            userPay.add(byUserIdAndGroupId.get());
                        }

                        userList.add(x);
                    }
                });

        groupCart.getParticipates().removeAll(userList);
       groupPayRepo.deleteAll(userPay);
    }

    private void makeFalseItemDelete(GroupCart groupCart) {
        List<GroupItem> usersToDelete = groupCart.getGroupItemList().stream()
                .filter(u -> Boolean.FALSE.equals(u.getIsConfirmed()))
                .toList();

        groupCart.getGroupItemList().removeAll(usersToDelete);
        }
        @Transactional
    public void OrderConfirmed(GroupCart groupCart) {
        Optional<GroupCart> groupCart1=groupCartRepo.findById(groupCart.getId());

        log.info("Payment option starts................................................................."+groupCart1.get().getHostUser().getId());
        GroupCart newCart=groupCart1.get();
        List<GroupPay>list=groupPayRepo.findGroupId(newCart.getId());
        List<GroupItem>groupItemList=groupCart.getGroupItemList();
        if(list.isEmpty()){
            return;
        }

        if(groupItemList.isEmpty()){
            throw new BadRequestException("No user can make a ready to pay ");
        }
        List<GroupPay>failuserPay=new ArrayList<>();
        List<GroupItem>failedItem=new ArrayList<>();
        for(GroupPay groupPay : list){
            if(groupPay.getPayStatus().equals(PaymentEnum.INITIATED)){
                for(GroupItem groupItem : groupItemList){
                    if(groupItem.getAddedBy().getId().equals(groupPay.getUserid())){
                        failedItem.add(groupItem);
                    }
                }
                failuserPay.add(groupPay);
            }
        }
        groupItemList.removeAll(failedItem);
list.removeAll(failuserPay);






        Order order=new Order();

        order.setUser(groupCart.getHostUser());
        order.setRestaurant(groupCart.getRestaurant());
        order.setDeliveryFees(new BigDecimal(groupCart.getDeliveryFees()));
        order.setLastpaymentTime(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        groupItemList.stream()
                .forEach(x->{
                    OrderItem orderItem=new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setItemName(x.getName());
                    orderItem.setItemPrice(BigDecimal.valueOf(x.getPrice()));
                    orderItem.setQuantity(x.getQuantity());
                    order.getItemList().add(orderItem);
                });
        Double itemPrice=0.0;
        for(GroupItem groupItem : groupItemList){
               itemPrice+=groupItem.getPrice();
        }
            order.setOrderItemCharge(new BigDecimal(itemPrice));
            order.setTotalGrant(new BigDecimal(itemPrice + groupCart.getDeliveryFees()));
            order.setOrderType(OrderType.GROUP);
            OrderDto orderDto = paymentService.orderSuccess(order, groupCart.getHostUser(), null);
            if (orderDto != null) {
                groupCart1.get().setOrderId(orderDto.getId());


                list.forEach(x -> {
                    Long id = x.getUserid();
                    User user = userRepo.findById(id).orElseThrow(() -> new ResourceNoFoundException("Not any resource is found"));
                    String email = user.getEmail();
                    mailsender.sendOtpGroupUser(email, x.getUserid(), groupCart);
                });


                groupCart1.get().setStatus(GroupCartStatus.COMPLETED);
            }
        }


}
