package com.arun.Restaurantbackend.Service;
import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Entity.Userprofile;
import com.arun.Restaurantbackend.Repository.OrderRepo;
import com.arun.Restaurantbackend.Repository.RestaurantRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Repository.UserprofileRepo;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    EmailProducer emailProducer;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    OrderService orderService;

    @Autowired
    RestaurantRepo restaurantRepo;


    @Autowired
    UserRepo userRepo;

    @Autowired
    UserprofileRepo userprofileRepo;

    @Autowired
    TimerService timerService;


    @Test
    void sendEmail(){

        Optional<Restaurant> byrestId = restaurantRepo.findByrestId(1L);

        Restaurant restaurant=byrestId.get();

        User user1=userRepo.findByEmail("tarun8219257264@gmail.com").orElse(null);
        User user2=userRepo.findByEmail("dummy452t@gmail.com").orElse(null);

        Userprofile userprofiles = userprofileRepo.findByuserid(user1.getId()).get().getFirst();
        Userprofile userprofiles1 = userprofileRepo.findByuserid(user2.getId()).get().getFirst();

        Order order1=new Order();
        order1.setUser(user1);
        order1.setTotalGrant(new BigDecimal(10));
        order1.setStatus(OrderEnum.PREPARING);
        order1.setAddress(userprofiles);
        order1.setRestaurant(restaurant);
        order1.setOrderAcceptTime(LocalDateTime.now().plusMinutes(10));
        order1.setOrderItemCharge(new BigDecimal(0));
        order1.setCreatedAt(LocalDateTime.now());

        Order order2=new Order();
        order2.setUser(user2);
        order2.setTotalGrant(new BigDecimal(10));
        order2.setStatus(OrderEnum.PREPARING);
        order2.setAddress(userprofiles1);
        order2.setRestaurant(restaurant);
        order2.setOrderAcceptTime(LocalDateTime.now().plusMinutes(10));
        order2.setOrderItemCharge(new BigDecimal(0));
        order2.setCreatedAt(LocalDateTime.now());
        order1.setOrderType(OrderType.NORMAL);
        order2.setOrderType(OrderType.NORMAL);

        order1.setEstimatekm(5.5);
        order1.setEstimatetime("5 mins");
        order1.setEstimatedeliverytime(LocalDateTime.now().plusMinutes(5));

        order2.setEstimatekm(5.5);
        order2.setEstimatetime("5 mins");
        order2.setEstimatedeliverytime(LocalDateTime.now().plusMinutes(5));


order2.setLastUpdateTime(LocalDateTime.now());
order1.setLastUpdateTime(LocalDateTime.now());
List<Order>list=new ArrayList<>();
list.add(order2);
list.add(order1);

log.info(restaurant.getId()+"  "+user1.getId()+"  "+user2.getId()+" "+userprofiles1.getId()+"  "+userprofiles.getId());





    }


    }