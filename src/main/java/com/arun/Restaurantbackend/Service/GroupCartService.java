package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.*;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.ConflictException;
import com.arun.Restaurantbackend.Exception.IllegalAccessException;
import com.arun.Restaurantbackend.Exception.InvalidRequestException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupCartService {

private final DELIVERYASSIGNSERVICE deliveryassignservice;
    private final GroupCartRepo groupCartRepo;
private final ModelMapper mapper;
    private final UserRepo userRepo;
    private final RestaurantRepo restaurantRepo;
    private final Validationhandler validationhandler;
private final SocietyNRepo societyRepo;
private final UserprofileRepo userprofileRepo;
    private final GroupPayRepo groupPayRepo;
private final TimerService timerService;
    @Transactional
    public GroupCartDto createGroupCart(CreateGroupCartRequest request) {
       User user1= validationhandler.finduser();
        User user= userRepo.findById(user1.getId())
                .orElseThrow(()->new ResourceNoFoundException("User Is not found"));


    System.out.println(".................."+user1);
        Restaurant restaurant=restaurantRepo.
                findById(request.getRestaurantId()).
                orElseThrow(()-> new ResourceNoFoundException("Restaurant is not found"));

        String unique=UUID.randomUUID()
                .toString().
                toUpperCase().
                substring(0,7);
        Userprofile byUserid = userprofileRepo.
                findByUserid(user.getId()).
                orElseThrow(()-> new
                        ResourceNoFoundException("UserPrfoile is not found"));
log.info(byUserid+"   ");
       SocietyN societyN= societyRepo.findBySocietyName(byUserid.getSocietyName()).
                orElseThrow(()-> new ResourceNoFoundException("Society is not exist"));

       log.info(societyN+" ........................................................................");

        GroupCart groupCart=new GroupCart();
groupCart.setSocietyId(societyN.getId());
        groupCart.setHostUser(user);
        groupCart.getParticipates().add(user);
        groupCart.setRestaurant(restaurant);
        groupCart.setStatus(GroupCartStatus.ACTIVE);
        groupCart.setUniqueId(unique);
        groupCart.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        groupCart.setPaymentAt(LocalDateTime.now().plusMinutes(3).plusSeconds(30));
        Double estimatetimekm=deliveryassignservice.findkm(societyN.getSocietyName(),restaurant.getTown());

if(estimatetimekm>15){
    throw new ConflictException("Service is not reachable");
}



        double decimal = 0.0;

        if(estimatetimekm<=5){
            decimal=50;
        }
        else if(estimatetimekm<=10){
            decimal=80;
        }
        else if(estimatetimekm<=15){
            decimal=130;
        }

        Long km=timerService.getNearestDeliveryBoy(groupCart.getRestaurant());

        if(km>5){
            decimal+=10;
        }
        else{
            decimal+=30;
        }
        groupCart.setDeliveryFees(decimal);




        GroupCart  save = groupCartRepo.save(groupCart);
        GroupPay groupPay=new GroupPay();
        groupPay.setGroupid(save.getId());
    groupPay.setTotalPrice(0);
    groupPay.setUserid(user.getId());
    groupPayRepo.save(groupPay);

        return mapper.map(save,GroupCartDto.class);
    }



    @Transactional
    public GroupCartDto joingroupcartrequest(JoinGroupCartRequest request) {
        GroupCart groupCart= groupCartRepo.findByIdAndStatus
               (request.getUniqueId(),GroupCartStatus.ACTIVE).
               orElseThrow(()->new ResourceNoFoundException("GroupCart not found"));
        User user1= validationhandler.finduser();
        Userprofile byUserid = userprofileRepo.findByUserid(user1.getId())
                .orElseThrow(()-> new ResourceNoFoundException("UserPrfoile is not found"));
        SocietyN society1= societyRepo.findById(groupCart.getSocietyId()).
                orElseThrow(()-> new ResourceNoFoundException("Society is not exist"));
        SocietyN society2= societyRepo.findBySocietyName(byUserid.
                                getSocietyName()).
                orElseThrow(()-> new ResourceNoFoundException("Society is not exist"));


        Optional<User> first = groupCart.getParticipates().stream()
                .filter(x -> x.getId().equals(user1.getId()))
                .findFirst();


        if(first.isPresent()){
            throw new ConflictException("User is already existed");
        }


        groupCart.getParticipates().add(user1);
        if(!society2.equals(society1)){
            throw  new InvalidRequestException(" Society is not same , mismatch");
        }
        GroupPay groupPay=new GroupPay();
        groupPay.setGroupid(groupCart.getId());
        groupPay.setTotalPrice(0);
        groupPay.setUserid(user1.getId());
        groupPayRepo.save(groupPay);
        return mapper.map(groupCart,GroupCartDto.class);
    }


    @Transactional
    public GroupCartDto additemtorest(AddGroupCartItemRequest request) {
       GroupCart groupCart= groupCartRepo.
                findByGroupIdAndStatus(request.getGroupCartId(),GroupCartStatus.ACTIVE)
                .orElseThrow(()->new ResourceNoFoundException("GroupCart not found"));
       User user=validationhandler.finduser();


       System.out.println(" User Credentails :   ->  "+ user);

        boolean equals = groupCart.getHostUser().equals(user);
        if(!equals){
            Optional<User> first = groupCart.getParticipates().stream()
                    .filter(x -> x.getId().equals(user.getId())).findFirst();
            if(first.isEmpty()){
                throw new InvalidRequestException("Invalid access , User is not defined");
            }
        }
        Restaurant restaurant = groupCart.getRestaurant();
        Item item = restaurant.getMenu()
                .getItemList()
                .stream()
                .filter(x -> x.getId().equals(request.getMenuitemId()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNoFoundException("Id is mismatched..."));
        Optional<GroupItem> first = groupCart.getGroupItemList()
                .stream().
                filter(x -> x.
                        getName()
                        .equals(item.getName())).findFirst();

        if(first.isPresent() && first.get().getAddedBy().equals(user)){
            first.get().setQuantity(first.get().getQuantity()+1);
            return mapper.map(groupCart,GroupCartDto.class);
        }

        GroupItem groupItem=new GroupItem();
        groupItem.setGroupCart(groupCart);
        groupItem.setAddedBy(user);
        groupItem.setName(item.getName());
        groupItem.setPrice(item.getPrice());
        groupItem.setQuantity(1);
        groupCart.getGroupItemList().add(groupItem);
        return mapper.map(groupCart,GroupCartDto.class);
    }


    @Transactional
    public GroupCartDto removeitemtorest(RemoveGroupCartItemRequest request) {
        GroupCart groupCart= groupCartRepo.
                findByGroupIdAndStatus(request.getGroupCartId(),GroupCartStatus.ACTIVE)
                .orElseThrow(()->new ResourceNoFoundException("GroupCart not found"));

        User user=validationhandler.finduser();

        boolean equals = groupCart.getHostUser().equals(user);

        if(!equals){
            Optional<User> first = groupCart.getParticipates().stream().filter(
                    x -> x.getId().equals(user.getId())).findFirst();

            if(first.isEmpty()){
                throw new InvalidRequestException("Invalid access , User is not defined");
            }

        }

        Optional<GroupItem> first = groupCart.getGroupItemList()
                .stream().
                filter(x -> x.
                        getId()
                        .equals(request.getGroupCartItemId())).findFirst();
      if(first.isEmpty()){
          throw new InvalidRequestException("Item is not existed in cart");
      }
      else{
      if(!first.get().getAddedBy().equals(user.getId())){
          throw new IllegalAccessException(" Not able to access or remove other user item");
      }
          groupCart.getGroupItemList().remove(first.get());
      }
        return mapper.map(groupCart,GroupCartDto.class);

    }
}
