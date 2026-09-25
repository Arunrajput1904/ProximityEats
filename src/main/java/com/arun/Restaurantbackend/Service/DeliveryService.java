package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.DTO.Estimatetimekm;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Handler.Mailsender;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.EmailType;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

@RequiredArgsConstructor
@Service
@Slf4j
public class DeliveryService {

    private final UserprofileRepo userprofileRepo;
    private final Mailsender mailsender;
    private final DeliveryBoyRepo deliveryBoyRepo;
   private final OrderRepo orderRepo;
   private final ModelMapper mapper;
   private final Validationhandler validationhandler;
   private final BoyEmailRepo boyEmailRepo;
private final EmailProducer emailProducer;
private final DELIVERYASSIGNSERVICE deliveryassignservice;

   @PreAuthorize("hasRole('DELIVERY_BOY')")
    public Optional<List<OrderDto>> getallorderservice() {

       User user=validationhandler.finduser();

       DeliveryBoy userprofile=deliveryBoyRepo.findByUserid(user.getId()).
               orElseThrow(()-> new ResourceNoFoundException("No delivery boy exist"));

        List<Order>orderList=orderRepo.findallbystatus(OrderEnum.PREPARED);

        List<Order>filterorder=new ArrayList<>();

        for(Order order : orderList){

            if(order.getOrderType().equals(OrderType.BUNDLE)){
                continue;
            }

           Duration duration=Duration.between(order.getOrderAcceptTime(),LocalDateTime.now());

           if(duration.getSeconds()>=60 && duration.getSeconds()<=120){
               Double findkm = deliveryassignservice.findkm(order.getRestaurant().getTown(), userprofile.getTown());
                       order.setDeliveryFees(order.getDeliveryFees().add(new BigDecimal(30)));
               if(findkm<=15){
                   filterorder.add(order);
               }

           }

           else if(duration.getSeconds()>=0 && duration.getSeconds()<=60) {
               Double findkm = deliveryassignservice.findkm(order.getRestaurant().getTown(), userprofile.getTown());

               if(findkm<=5){
                   filterorder.add(order);
               }
           }
        }

        return Optional.of(filterorder.stream().
                map(item-> mapper.
                        map(item,OrderDto
                                .class)).collect(Collectors
                        .toList()));

   }


    @Transactional
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public OrderDto getorderacceptbyid(Long id) {
        Order order=orderRepo.findByIdAndstatus(id,OrderEnum.PREPARED).orElseThrow(()-> new ResourceNoFoundException("not found order"));
        User user=validationhandler.finduser();
        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid(user.getId()).orElseThrow(()->
                new ResourceNoFoundException("not found "));
        Estimatetimekm estimatetimekm=deliveryassignservice.
                findestimatedistanceandtime(order.getRestaurant().getTown(),deliveryBoy.getTown());
        if(estimatetimekm.getEstkm()>15){
            throw new ConflictException("Delivery boy must be in range of 10km");
        }
      order.setResttoboyloc(estimatetimekm.getStringBuilder());
        if(order.getOrderAcceptTime().isBefore(LocalDateTime.now())){
            throw new ConflictException("order session is expired");
        }
        long minute = Duration.between( LocalDateTime.now(), order.getEstimatedeliverytime()).toMinutes();
        order.setEstimatetime(minute+ "  min");
        order.setStatus(OrderEnum.READY_FOR_PICKUP);
        deliveryBoy.setOrderid(order.getId());
        deliveryBoy.setStatus(StatusEnum.BUSY);
        return mapper.map(order,OrderDto.class);
    }

    @Transactional
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public OrderDto takepickup(Long id){
        Order order=orderRepo.findByIdAndstatus(id,OrderEnum.READY_FOR_PICKUP).orElseThrow(()-> new ResourceNoFoundException("not found"));
        User user=validationhandler.finduser();

        EmailEvent emailEvent=new EmailEvent();
        emailEvent.setUser(user);
        emailEvent.setOrder(order);
        emailEvent.setType(EmailType.OTP);

        emailProducer.produce(emailEvent);
        long minute = deliveryassignservice.findminutes(order.getEstimatekm());
        order.setEstimatetime(minute+ "  min");
        order.setStatus(OrderEnum.OUT_FOR_DELIVERY);
        return mapper.map(order,OrderDto.class);
   }






    @PreAuthorize("hasRole('DELIVERY_BOY')")
    @Transactional
    public OrderDto takedelivered(String otp,Long id){
       Order order=orderRepo.findByIdAndstatus(id,OrderEnum.OUT_FOR_DELIVERY).orElseThrow(()-> new ResourceNoFoundException("not found"));
        User user=validationhandler.finduser();
        Deliveryboyemail deliveryboyemail= boyEmailRepo.findByUserid(user.getId()).orElseThrow(()-> new ResourceNoFoundException("No found"));
        if(!(deliveryboyemail.getCurrentpassword().equals(otp))){
            Long count=deliveryboyemail.getCount()+1;
if(count<=2){
    boyEmailRepo.save(deliveryboyemail);
    throw new BadRequestException("Invalid otp,try again");
}
            order.setStatus(OrderEnum.REFUND);
orderRepo.save(order);
throw new BadRequestException("Session expired, order cancel");
        }
        order.setEstimatetime("  ");
         order.setStatus(OrderEnum.DELIVERED);
        mailsender.sendConfirmationmessage(order.getUser().getEmail(),order,"Delivered");
        boyEmailRepo.deletepervious(deliveryboyemail.getId());

        orderRepo.save(order);
        return mapper.map(order,OrderDto.class);}


//    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public OrderDto getorderbyid(Long id) {
       Order order=orderRepo.findByIdmethod(id).
               orElseThrow(()-> new ResourceNoFoundException("not found"));
        User user=validationhandler.finduser();
        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid
                (user.getId()).orElseThrow(()-> new ResourceNoFoundException("not found "));
        if(!deliveryBoy.getOrderid().equals(order.getId())){
            throw new BadRequestException("Delivery boy is not a owner of this order");
        }
        return mapper.map(order,OrderDto.class);
   }


}
