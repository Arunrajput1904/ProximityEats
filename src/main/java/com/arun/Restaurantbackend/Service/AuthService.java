package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.*;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.ResourceAlreadyExistsException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Handler.Mailsender;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.CartEnum;
import com.arun.Restaurantbackend.Utilis.RoleEnum;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Optional;

//import com.arun.Restaurantbackend.DTO.UsersDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    final private UserRepo userRepo;
final  private ModelMapper mapper;
final  private PasswordEncoder passwordEncoder;
final private UserService userService;
private final CloudinaryService cloudinaryService;
final private SessionService sessionService;
final  private JwtService jwtcreation;
final  private  AuthenticationManager authenticationManager;
final private RoleRepo roleRepo;
final private PermissionRepo permissionRepo;
final  private UserprofileRepo userprofileRepo;
final private DeliveryBoyRepo deliveryBoyRepo;
final private RestaurantRepo restaurantRepo;
final private ManagerProfileRepo managerProfileRepo;
final private Mailsender mailsender;
private final EmailvalidationRepo emailvalidationRepo;
private final CartRepo cartRepo;
private final SocietyRepo societyRepo;
@Transactional
public UserDto  SignupPath(Userobject usersDto) {
    Long id=usersDto.getRoleid();
    Optional<User> userOptional=userRepo.findByEmail(usersDto.getEmail());

    System.out.println("Exception processing");
    if(userOptional.isPresent()){

throw new ResourceAlreadyExistsException("Account is Already register ");
    }

    System.out.println("Exception proceed ");

User user1=mapper.map(usersDto,User.class);
    user1.setPassword(passwordEncoder.encode(user1.getPassword()));
if(user1.getEmail().equals("arunrajput16177@gmail.com")){
    Role role=roleRepo.findByType("ADMIN").orElseThrow(()->new ResourceNoFoundException("Role is not in db"));
    user1.setProfileComplete(true);
    user1.setRole(role);
}
else{
    Role role=roleRepo.findById(id).orElseThrow(()->new ResourceNoFoundException("Not found"));
    user1.setRole(role);
}

Wallet wallet=new Wallet();
wallet.setUser(user1);
wallet.setBalance(0.0);
user1.setWallet(wallet);
user1.setCancelApproveTime(0l);
User user=userRepo.save(user1);
//System.out.println(user);
return mapper.map(user,UserDto.class);
    }



    public Optional<UserAuth> loginUsers(loginUser user) {


        UsernamePasswordAuthenticationToken usertoken = null;



        usertoken = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());

        Authentication authentication = authenticationManager.authenticate(usertoken);


        User user1=(User) authentication.getPrincipal();



        return  Optional.of(new UserAuth());



    }
    public void changeuser( ChangeDetailsDto user)  {

        OAuth2User oAuth2User =(OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long id=user.getRoleid();

        String email=oAuth2User.getAttribute("email");

        User userr=userRepo.findByEmail(email).orElseThrow(()->new ResourceNoFoundException("Not found in db"));


        Field []changefields=ChangeDetailsDto.class.getDeclaredFields();

        Field[] Userfields=User.class.getDeclaredFields();


 try{
     for(Field changefield: changefields){
         for(Field Userfield:Userfields){
             if(Userfield.getName().equals(changefield.getName())){
                 changefield.setAccessible(true);
                 Userfield.setAccessible(true);
                 Userfield.set(userr,changefield.get(user));
             }

         }

     }
 }
 catch ( IllegalAccessException exception){
     throw new com.arun.Restaurantbackend.Exception.IllegalAccessException("Illegal access of reflection");
 }


        if(userr.getEmail().equals("arunrajput16177@gmail.com")){
            Role role=roleRepo.findByType("ADMIN").orElseThrow(
                    ()->new ResourceNoFoundException("Role is not found"));
            userr.setRole(role);

        }
        else{
            Role role=roleRepo.findById(id).orElseThrow(()->new ResourceNoFoundException("Not found"));
            userr.setRole(role);
        }


        Wallet wallet=new Wallet();
        wallet.setUser(userr);
        wallet.setBalance(0.0);
        userr.setWallet(wallet);
        userr.setCancelApproveTime(0l);
        userRepo.save(userr);
}
        @PreAuthorize("hasRole('ADMIN')")
    public Role rolepermissionmap(Long rId, Long pId) {

    Role role=roleRepo.findById(rId).orElseThrow(()->new ResourceNoFoundException("Not found Role"));
    Permission permission=permissionRepo.findById(pId).orElseThrow(()->new ResourceNoFoundException("Not found permission"));


    role.getPermissionList().add(permission);
    permission.getRoleList().add(role);

    return roleRepo.save(role);

    }


    public void roleProvider(HttpServletRequest request, HttpServletResponse response) {

        Object object =SecurityContextHolder.getContext().getAuthentication();
        User user=null;
        if(object instanceof  UsernamePasswordAuthenticationToken  usertoken){
            user=(User) usertoken.getPrincipal();
        }
        else if(object instanceof OAuth2AuthenticationToken auth){
            OAuth2User auth2User=auth.getPrincipal();
            String email=auth2User.getAttribute("email");
            user=userRepo.findByEmail(email).orElseThrow(()->new AccessDeniedException("Not found"));
        }

        try {


            if(user.getRole().getType().equals(RoleEnum.ADMIN.toString())){
                response.sendRedirect("/mainpage");
                return;
            }



            if(user.getRole().getType().equals(RoleEnum.USER.toString())){
                response.sendRedirect("/userprofile.html");
            }
            else  if(user.getRole().getType().equals(RoleEnum.DELIVERY_BOY.toString())){
                response.sendRedirect("/deliveryBoy.html");
            }
            else{
                response.sendRedirect("/manager.html");
            }

        }
        catch (Exception exception){
            throw new IllegalArgumentException("Role is  not valid");
        }
    }


    public User completeuserprofile(Userprofiledto userprofiledto,User user, HttpServletResponse httpServletResponse) {

    Userprofile userprofile=mapper.map(userprofiledto,Userprofile.class);

        userprofile.setUserid(user.getId());

        user.setProfileComplete(true);

        userRepo.save(user);


        userprofileRepo.save(userprofile);


        Cart cart=new Cart();

        cart.setUserId(user.getId());

        cart.setStatus(CartEnum.ACTIVE);

        cartRepo.save(cart);

        return user;
    }

    public User completedeliveryprofile(DeliveryBoyDto deliveryBoyDto, User user) {
    DeliveryBoy deliveryBoy=mapper.map(deliveryBoyDto,DeliveryBoy.class);
    deliveryBoy.setUser(user);

    user.setProfileComplete(true);
System.out.println(deliveryBoy);
         deliveryBoyRepo.save(deliveryBoy);
        Cart cart=new Cart();
        cart.setUserId(user.getId());
        cart.setStatus(CartEnum.ACTIVE);
        cartRepo.save(cart);
        userRepo.save(user);



         return user;

    }





    public User managerprofile(Managerrestprofile managerProfileDto, User user) {
    user.setProfileComplete(true);


        ManagerProfile managerProfile=mapper.map(managerProfileDto,ManagerProfile.class);
    Restaurant restaurant=mapper.map(managerProfileDto.getRestaurant(),Restaurant.class);
    restaurant.setStatus(String.valueOf(StatusEnum.PENDING));
        restaurant.setManagerProfile(managerProfile);
        restaurant.getMenu().setRestaurant(restaurant);


        for (Item item : restaurant.getMenu().getItemList()) {
            item.setMenu(restaurant.getMenu());

        }
        managerProfile.setUser(user);
        Cart cart = new Cart();
        cart.setUserId(user.getId());
        cart.setStatus(CartEnum.ACTIVE);
        cartRepo.save(cart);
        restaurant.setCancelApproveTime(0l);
        restaurantRepo.save(restaurant);
        return userRepo.save(user);

    }


}
