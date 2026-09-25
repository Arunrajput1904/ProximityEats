package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.Estimatetimekm;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.DTO.Orderaddress;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Entity.CancelEntity;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//import com.arun.Restaurantbackend.Repository.CancelEntity;
//import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService{
    private final DeliveryBoyRepo deliveryBoyRepo;


    private final OrderRepo orderRepo;
private final ModelMapper mapper;
private final CartRepo cartRepo;
private final Validationhandler validationhandler;
private final UserprofileRepo userprofileRepo;
private final RestaurantRepo restaurantRepo;
private final ItemRepo itemRepo;
private final DELIVERYASSIGNSERVICE deliveryassignservice;
private final TimerService timerService;
@PersistenceContext
private final EntityManager entityManager;

    @PreAuthorize("hasRole('MANAGER')")
    public Optional<List<OrderDto>> findallbyrest(Long id) {
        List<Order> orderlist=orderRepo.findallbyrest(id);
        List<OrderDto>dtoList=orderlist.stream().map(item-> mapper.map(item,OrderDto.class) ).collect(Collectors.toList());
        return Optional.of(dtoList);

    }
    @PreAuthorize("hasRole('USER')")
    public Optional<List<OrderDto>> findallbyuser(Long id) {

        List<Order> orderlist=orderRepo.findallbyuser(id);


        List<OrderDto>dtoList=orderlist.stream().map(item-> mapper.map(item,OrderDto.class) ).collect(Collectors.toList());
        return Optional.of(dtoList);
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public OrderDto makeorder(User user, Orderaddress address) {

        Userprofile Useraddress=userprofileRepo.findById(address.getAddressId())
                .orElseThrow(()-> new ResourceNoFoundException("No resource found"));
        if(!Objects.equals(Useraddress.getUserid(), user.getId())){
            throw new AccessDeniedException("Invalid address");
        }
        Cart cart=cartRepo.findByUSerIdAndStatus(user.getId(), CartEnum.ACTIVE).orElseThrow(()-> new ResourceNoFoundException("Not found cart"));
        if(cart.getRestaurantid()==null){
            throw new UnprocessableEntityException("Cart is Empty....");
        }
        Restaurant restaurant=restaurantRepo.findById(cart.getRestaurantid()).orElseThrow(()->
                new ResourceNoFoundException("no restaurant found"));
        Estimatetimekm estimatetimekm =deliveryassignservice.findestimatedistanceandtime(Useraddress.getSocietyName(),
                restaurant.getTown());


        if(estimatetimekm.getEstkm()>deliveryassignservice.maxKm){
            throw new ConflictException("Delivery unreachable, sorry....");
        }


        Order order =new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setAddress(Useraddress);
        order.setEstimatekm(estimatetimekm.getEstkm());
        order.setEstimatedeliverytime(estimatetimekm.getEstimatetime());
        long minutes = Duration.between( LocalDateTime.now(),estimatetimekm.getEstimatetime()).toMinutes();
        order.setEstimatetime(minutes+" min");
        entityManager.persist(order);
        BigDecimal decimal = null;
        Long km=timerService.getNearestDeliveryBoy(restaurant);

        if(estimatetimekm.getEstkm()<=5){
            decimal=new BigDecimal("50");
        }
        else if(estimatetimekm.getEstkm()<=10){
            decimal=new BigDecimal("80");
        }
        else if(estimatetimekm.getEstkm()<=15){
            decimal=new BigDecimal("130");
        }

        if(km>5){
            decimal.add(new BigDecimal(20));
        }
        else{
            decimal.add(new BigDecimal(30));
        }


         order.setDeliveryFees(decimal);

        final BigDecimal[] itemtotalprice = {new BigDecimal("0.0")};

        List<Cartitem>orderItemList=cart.getList();
        orderItemList.stream().forEach(item->{
            log.info(item+"                    ..............................................cart item ");
           Long id= item.getItemid();
           Item item1=itemRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("item is not found"));
           if(item1.getAction().equals(ItemAction.ACTIVE)) {
               OrderItem orderItem = new OrderItem();
               orderItem.setOrder(order);
               orderItem.setItemName(item1.getName());
               orderItem.setItemPrice(BigDecimal.valueOf(item1.getPrice()));
               orderItem.setQuantity(item.getQuantity());
               BigDecimal totalcredit = orderItem.getItemPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
               itemtotalprice[0] = itemtotalprice[0].add(totalcredit);
               order.getItemList().add(orderItem);
           }
        });
        log.info(itemtotalprice[0]+" ...............................................................................");
        order.setStatus(OrderEnum.PAYMENT_PENDING);
        order.setOrderItemCharge(itemtotalprice[0]);
        log.info("........."+ itemtotalprice[0] +" ........."+order.getDeliveryFees());
        BigDecimal bigDecimal=new BigDecimal("0.0");
        bigDecimal=bigDecimal.add(order.getOrderItemCharge());
        bigDecimal=bigDecimal.add(order.getDeliveryFees());
        log.info("........."+bigDecimal);
        order.setTotalGrant(bigDecimal);
        order.setLastpaymentTime(LocalDateTime.now().plusMinutes(2));
        order.setOrderType(OrderType.NORMAL);
order.setUsertorestloc(estimatetimekm.getStringBuilder());
        orderRepo.save(order);
return mapper.map(order,OrderDto.class);

    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public OrderDto cancelorder(Long id) {
        Order order=orderRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Not found order for cancelation"));
            User user1=validationhandler.finduser();
         if(!user1.getUsername().equals(order.getUser().getUsername())){
             throw new AccessDeniedException("Invalid access");
         }

        if(order.getStatus().ordinal()>=2){
            throw new ConflictException("Not able to cancel the order, order is prepared already");
        }
        order.setStatus(OrderEnum.PROCESSING_REFUND);
        User user=validationhandler.finduser();
        CancelEntity cancel=new CancelEntity();
        cancel.setUserid(user.getId());
        cancel.setOrderid(order.getId());
        cancel.setReason(RoleEnum.USER);
        order.setCancel(cancel);

        return mapper.map(order,OrderDto.class);

    }


    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public OrderDto cancelorderbyrest(Long id) {

        Order order=orderRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Not found order for cancelation"));
        if(order.getStatus().equals(OrderEnum.OUT_FOR_DELIVERY) || order.getStatus().equals(OrderEnum.READY_FOR_PICKUP)){
            throw new AccessDeniedException("Not able to cancel the out of de;livery order");
        }
        User user1=validationhandler.finduser();
        if(!user1.getUsername().equals(order.getRestaurant().getManagerProfile().getUser().getUsername())){
            throw new AccessDeniedException("Invalid access");
        }
        order.setStatus(OrderEnum.PROCESSING_REFUND);
        User user=validationhandler.finduser();
        CancelEntity cancel=new CancelEntity();
        cancel.setUserid(user.getId());
        cancel.setOrderid(order.getId());
        cancel.setReason(RoleEnum.MANAGER);
        order.setCancel(cancel);

        return mapper.map(order,OrderDto.class);

    }

    @Transactional
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public OrderDto cancelybydelivery(Long id) {
        Order order=orderRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Not found order for cancelation"));
        if(!order.getStatus().equals(OrderEnum.OUT_FOR_DELIVERY)){
            throw new AccessDeniedException("Not able to cancel the order");
        }
        User user1=validationhandler.finduser();
        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid(user1.getId()).orElseThrow(()-> new ResourceNoFoundException("Not a delivery boy access"));

        if(!deliveryBoy.getUser().getUsername().equals(order.getUser().getUsername())){
            throw new AccessDeniedException("Invalid access");
        }
        order.setStatus(OrderEnum.PROCESSING_REFUND);
        User user=validationhandler.finduser();
        CancelEntity cancel=new CancelEntity();
        cancel.setUserid(user.getId());
        cancel.setOrderid(order.getId());
        cancel.setReason(RoleEnum.DELIVERY_BOY);
        order.setCancel(cancel);
        return mapper.map(order,OrderDto.class);
    }
}
