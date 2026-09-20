package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.Entity.ManagerProfile;
import com.arun.Restaurantbackend.Entity.Restaurant;
import com.arun.Restaurantbackend.Entity.User;

import com.arun.Restaurantbackend.Repository.ManagerProfileRepo;
import com.arun.Restaurantbackend.Repository.RestaurantRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.websocket.pojo.PojoEndpointBase;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Validationhandler {

    private final UserRepo userRepo;
private final RestaurantRepo restaurantRepo;
private final ManagerProfileRepo managerProfileRepo;

    boolean valid(Long id){
        User user1=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user2=userRepo.findById(id).orElse(null);

        if(user1.getId().equals(user2.getId())){
            return true;
        }
        return false;


    }

   public boolean validaterest(Long id){
        Object object =SecurityContextHolder.getContext().getAuthentication();
        User user=null;
        if(object instanceof  UsernamePasswordAuthenticationToken usertoken){
            user=(User)usertoken.getPrincipal();
        }
        else if(object instanceof OAuth2AuthenticationToken auth){
            OAuth2User auth2User=auth.getPrincipal();
            String email=auth2User.getAttribute("email");
            user=userRepo.findByEmail(email).orElse(null);

        }

        if(user==null){
            return false;
        }

//        if(user!=null && user.getRole()!="")

        Optional<ManagerProfile> managerProfile=managerProfileRepo.findByUseremail(user.getEmail());

        if(managerProfile.isEmpty()){
      log.info(" .....................................occ");
            return false;
        }

        log.info("..........................Occured"+managerProfile.get()+"  "+user);

        Optional<Restaurant>restaurant=restaurantRepo.findById(id);

        if(restaurant.isEmpty()){
            return false;
        }

        if(!(restaurant.get().getStatus().equals(String.valueOf(StatusEnum.ACTIVE)))){
            log.info("...........................................");
            return false;
        }

        if(managerProfile.get().getId().equals(restaurant.get().getManagerProfile().getId())){
            return true;
        }

        return false;


    }

    public User finduser(){
        Object object =SecurityContextHolder.getContext().getAuthentication();
        User user=null;
        if(object instanceof  UsernamePasswordAuthenticationToken usertoken){
            user=(User)usertoken.getPrincipal();
        }
        else if(object instanceof OAuth2AuthenticationToken auth){
            OAuth2User auth2User=auth.getPrincipal();
            String email=auth2User.getAttribute("email");
            user=userRepo.findByEmail(email).orElse(null);

        }
        return user;
    }

}
