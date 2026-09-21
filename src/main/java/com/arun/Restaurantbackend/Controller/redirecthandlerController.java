package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Service.JwtService;
import com.arun.Restaurantbackend.Service.SessionService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
@Hidden
@Slf4j
@RestController
@RequiredArgsConstructor
public class redirecthandlerController {
    private final UserprofileRepo userprofileRepo;

    private  final JwtService jwtcreation;
    private final UserRepo userRepo;
    private  final SessionService sessionService;
private final SocietyNRepo societyNRepo;
            @GetMapping("/mainpage")
            public UserAuth respons(HttpServletRequest request, HttpServletResponse response) throws IOException {
                Object object = SecurityContextHolder.getContext().getAuthentication();
                User user = null;
                if (object instanceof UsernamePasswordAuthenticationToken usertoken) {
                    user = (User) usertoken.getPrincipal();
                } else if (object instanceof OAuth2AuthenticationToken auth) {
                    OAuth2User auth2User = auth.getPrincipal();
                    String email = auth2User.getAttribute("email");
                    user = userRepo.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Not found"));
        }
        String token1 = jwtcreation.jwtAccesstoken(user);
        String token2 = jwtcreation.jwtRefreshtoken(user);
        Cookie cookie1 = new Cookie("accesstoken", token1);
        cookie1.setPath("/");
        cookie1.setHttpOnly(true);
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("refreshtoken", token2);
        cookie2.setHttpOnly(true);
        cookie2.setPath("/");
        response.addCookie(cookie2);
Optional<Userprofile> userprofile= userprofileRepo.findByUserid(user.getId());
if(userprofile.isPresent()) {
    SocietyN societyN =societyNRepo.findBySocietyName((userprofile.get().getSocietyName())).orElseThrow(()->
            new ResourceNoFoundException("Not  society  exist"));
    Cookie cookie = new Cookie("userId",user.getId().toString());
    response.addCookie(cookie);
    Cookie cookie5 = new Cookie("societyId",societyN.getId().toString());
    response.addCookie(cookie5);

}
        response.sendRedirect("/swagger-ui/index.html");
                sessionService.sessioncreation(user, token2);
                return new UserAuth(token1, token2);

            }


  }
