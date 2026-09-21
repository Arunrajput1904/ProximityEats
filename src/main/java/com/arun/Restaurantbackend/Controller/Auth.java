package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.*;
import com.arun.Restaurantbackend.Entity.SocietyN;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Entity.UserAuth;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Repository.SocietyNRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Service.AuthService;
import com.arun.Restaurantbackend.Service.EmailProducer;
import com.arun.Restaurantbackend.Utilis.EmailType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1.  Authenication Apis", description = "Signup, login ")
public class Auth {
    private  final  AuthService authService;
        private  final UserRepo userRepo;
       private  final ModelMapper mapper;
       private final SocietyNRepo societyNRepo;
    private final EmailProducer producer;
    @PostMapping("/signup")
   public ResponseEntity<UserDto> makeSignup(@RequestBody  @Valid Userobject userDto)  {
        UserDto userDto1= authService.SignupPath(userDto);
        return ResponseEntity.ok(userDto1);
    }

    @GetMapping("/email/{id}")
    void sendmail(@PathVariable("id") String id){
        EmailEvent emailEvent=new EmailEvent();
        emailEvent.setTo(id);
        emailEvent.setType(EmailType.OTP);
        producer.produce(emailEvent);

    }
    @GetMapping("/complete-profile")
    public  ResponseEntity<MessageDto>  Roleprovider(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.roleProvider(request,response);
        return ResponseEntity.ok(new MessageDto("User is assgin with there appropriate role"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuth> loginuser(@RequestBody   @Valid loginUser loginUser ){
         UserAuth auth=authService.loginUsers(loginUser).orElseThrow(()-> new BadCredentialsException("Not found"));
         return ResponseEntity.ok(auth);
    }

    @PostMapping("/change")
    public ResponseEntity<MessageDto> addcreditails(@RequestBody  @Valid   ChangeDetailsDto changeDetailsDto, HttpServletResponse response){
      authService.changeuser(changeDetailsDto);
           return ResponseEntity.ok(new MessageDto("User details change successfully"));
    }

    @PostMapping("/profilecomplete")
    public  ResponseEntity<UserDto> completeprofile(@RequestBody   @Valid  Object profile, HttpServletResponse httpServletResponse){

        Object object =SecurityContextHolder.getContext().getAuthentication();
        User user=null;
        if(object instanceof  UsernamePasswordAuthenticationToken usertoken){
            user=(User)usertoken.getPrincipal();
        }
        else if(object instanceof OAuth2AuthenticationToken auth){
            OAuth2User auth2User=auth.getPrincipal();
            String email=auth2User.getAttribute("email");
            user=userRepo.findByEmail(email).orElseThrow(()->new ResourceNoFoundException("Not found"));
        }
        if(user.getRole().getType().equals("USER")){
            Userprofiledto userprofiledto=mapper.map(profile,Userprofiledto.class);
            if(checkcity(userprofiledto.getSocietyName())==false){
                log.info("/...................errror");
                throw new UnprocessableEntityException("Service not available in this city");

            }
           user= authService.completeuserprofile(userprofiledto,user,httpServletResponse);
        }
        else if(user.getRole().getType().equals("DELIVERY_BOY")){
            DeliveryBoyDto deliveryBoyDto=mapper.map(profile,DeliveryBoyDto.class);
            if(checkcity(deliveryBoyDto.getTown())==false){
                throw new UnprocessableEntityException("Service not available in this city");
            }
            user=authService.completedeliveryprofile(deliveryBoyDto,user);
        }
        else {
            Managerrestprofile Managerrestprofile=mapper.map(profile,Managerrestprofile.class);
            if(checkcity(Managerrestprofile.getRestaurant().getTown())==false){ throw new UnprocessableEntityException("Service not available in this city");
            }
            user=authService.managerprofile(Managerrestprofile,user);
        }
        UserDto userdto=mapper.map(user,UserDto.class);
        return  ResponseEntity.ok(userdto);
    }

    private boolean checkcity(String city) {
        List<SocietyN>cities=societyNRepo.findAll();
        AtomicBoolean valid= new AtomicBoolean(false);
        cities.stream().forEach(citie-> {
            if(citie.getSocietyName().toUpperCase().equals(city.toUpperCase())){
                valid.set(true);
            }
        });

        return valid.get();
    }



}
