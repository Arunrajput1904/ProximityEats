package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.*;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.AccessDeniedException;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Service.GroupCartService;
import com.arun.Restaurantbackend.Service.Validationhandler;
import com.arun.Restaurantbackend.Utilis.GroupCartStatus;
import com.arun.Restaurantbackend.Utilis.PaymentEnum;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@SecurityRequirement(name = "bearerAuth")

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
@Tag(name = "14. Group Apis", description = "Join/leave group cart , Add/delete  item in cart ")
public class GroupCartController {
    private final GroupOtpTrackRepo groupOtpTrackRepo;
    private final GroupItemRepo groupItemRepo;
    private final GroupPayRepo groupPayRepo;
    private final GroupCartRepo groupCartRepo;
private final Validationhandler validationhandler;
    private final GroupCartService groupCartService;


    @PostMapping("/creategroupcart")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<GroupCartDto> createGroupCart(@RequestBody @Valid CreateGroupCartRequest request){
        GroupCartDto groupCartDto=groupCartService.createGroupCart(request);
        return new  ResponseEntity<>(groupCartDto, HttpStatus.CREATED);
    }


    @PostMapping("/joingroup")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<GroupCartDto> joingroup(@RequestBody @Valid JoinGroupCartRequest request){


        GroupCartDto groupCartDto=groupCartService.joingroupcartrequest(request);
        return new  ResponseEntity<>(groupCartDto, HttpStatus.OK);
    }



    @PostMapping("/addItem")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<GroupCartDto> additemtorest(@RequestBody @Valid AddGroupCartItemRequest request){
        GroupCartDto groupCartDto=groupCartService.additemtorest(request);
        return new  ResponseEntity<>(groupCartDto, HttpStatus.CREATED);
    }

    @PostMapping("/removeItem")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<GroupCartDto> removeitemtorest(@RequestBody @Valid RemoveGroupCartItemRequest request){
        GroupCartDto groupCartDto=groupCartService.removeitemtorest(request);
        return new  ResponseEntity<>(groupCartDto, HttpStatus.OK);
    }

    @GetMapping("/paydone/{Gid}/{Uid}")
    @Transactional
    public void maketruestatus(@PathVariable("Gid") Long groupCart,@PathVariable("Uid") Long userid){
        GroupPay groupPay=groupPayRepo.findByUserIdAndGroupId(groupCart,userid)
                .orElseThrow(()-> new ResourceNoFoundException("Not found pay group"));
        groupPay.setPayStatus(PaymentEnum.INITIATED);
        GroupCart groupCart1=groupCartRepo.findById(groupCart).orElse(null);

        System.out.println(groupCart1.getGroupItemList()+" "+userid);
        for (GroupItem groupItem : groupCart1.getGroupItemList()) {
            if(groupItem.getAddedBy().getId().equals(userid)){
                groupItem.setIsConfirmed(true);
            }
        }
groupItemRepo.saveAll(groupCart1.getGroupItemList());
        log.info("User is Pay done"+"            ......................................");
    }

    @GetMapping("/joincartshow")
    @PreAuthorize("hasRole('USER')")
    public void makeJoinuser(@ModelAttribute("JoinUser") JoinUser user){
        GroupCart groupCart=groupCartRepo.findById(user.getGroupId())
                .orElseThrow(()-> new ResourceNoFoundException("No resource is found"));
        JoinGroupCartRequest joinGroupCartRequest=new JoinGroupCartRequest();
        joinGroupCartRequest.setUniqueId(groupCart.getUniqueId());
        groupCartService.joingroupcartrequest(joinGroupCartRequest);

        log.info("joincartshow"+".............................................................................");
    }

    @GetMapping("/paiduser/{Gid}/{Uid}")
    public void makePaystatus(@PathVariable("Gid") Long groupCart,@PathVariable("Uid") Long userid){
        GroupPay groupPay=groupPayRepo.findByUserIdAndGroupId(groupCart,userid)
                .orElseThrow(()-> new ResourceNoFoundException("Not found pay group"));
        groupPay.setPayStatus(PaymentEnum.PAID);
        groupPayRepo.save(groupPay);
    }


    @PostMapping("/validateuser/{userid}/{otp}")
    public ResponseEntity<?> validusersuborder(@PathVariable Long userid,@PathVariable String otp){
        GroupOtpTrack track = groupOtpTrackRepo.findByUserid(userid).orElseThrow(() -> new AccessDeniedException("User is not Afflicated to this order"));
        User finduser = validationhandler.finduser();
        if(!track.getGroupCart().getHostUser().getId().equals(finduser.getId())){
            throw new AccessDeniedException("Host user is not same ");
        }

        if(!track.getOtp().toLowerCase().equals(otp)){
            throw new BadRequestException("Otp is not valid");
        }

        track.setSuccess(true);
        groupOtpTrackRepo.save(track);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/ordernotreceived/{id}")
    @Transactional
    public void getrefundoforder(@PathVariable Long id){
        User finduser = validationhandler.finduser();
        GroupCart groupCart = groupCartRepo.findById(id).orElseThrow(() ->
                new ResourceNoFoundException("Invalid group id"));
        if(groupCart.getStatus().equals(GroupCartStatus.CANCELLED)){
            Optional<GroupPay> byUserIdAndGroupId = groupPayRepo.findByUserIdAndGroupId(groupCart.getId(), finduser.getId());
            if(byUserIdAndGroupId.isPresent()){
                Wallet wallet=finduser.getWallet();

                wallet.setBalance(wallet.getBalance()+byUserIdAndGroupId.get().getTotalPrice());

                groupCart.getHostUser().getWallet().setBalance(groupCart.getHostUser().getWallet().getBalance()-byUserIdAndGroupId.get().getTotalPrice());
            }
        }
    }





}
