package com.arun.Restaurantbackend.Handler;

import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Service.JwtService;
import com.arun.Restaurantbackend.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Successsessionhandler  extends SimpleUrlAuthenticationSuccessHandler {

    private  final JwtService jwtcreation;
private  final UserRepo userRepo;
private final UserService userService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        User user=null;
        if(authentication instanceof UsernamePasswordAuthenticationToken token){
             user = (User) token.getPrincipal();
        }
        else if(authentication instanceof OAuth2AuthenticationToken auth){
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = token.getPrincipal();
            String email=oauth2User.getAttribute("email");
            String name=oauth2User.getAttribute("name");
            String password=oauth2User.getAttribute("password");

            user=userRepo.findByEmail(email).orElse(null);

            if(user!=null && !(user.getPassword()==null)){
                request.getSession().invalidate();
                throw new BadCredentialsException("THis account is already exist");

            }
            if(user==null){
                user=User.builder().email(email).name(name).build();
                user=userService.savess(user);
            }

        }
        if(user.getPhoneNumber()==null && user.getRole()==null){
        response.sendRedirect("/change.html");
         }
     else{
        if(user.getProfileComplete().equals(false) || user.getProfileComplete()==null){
         response.sendRedirect("/auth/complete-profile");
        }
        else {
        response.sendRedirect("/mainpage");
    }
    }
    }
}
