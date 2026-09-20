package com.arun.Restaurantbackend.Handler;

import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.ExceptionHandler.authenicationexceptionHandler;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Service.JwtService;
import com.arun.Restaurantbackend.Service.SessionService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class jwtfilterhandler extends OncePerRequestFilter {

    private final JwtService jwtcreation;
    private final SessionService sessionService;
    private final    UserRepo userRepo;
    private final authenicationexceptionHandler authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


       Cookie [] cookies=request.getCookies();
       if(cookies==null){
           filterChain.doFilter(request,response);
           return;
       }

        try {
       String accesstoken= Arrays.stream(cookies).
               filter(item->item.getName().equals("accesstoken"))
               .map(item->item.getValue()).findFirst().orElse(null);

       String refreshtoken= Arrays.stream(cookies)
               .filter(item->item.getName()
                       .equals("refreshtoken")).
               map(item->item.getValue()).findFirst().orElse(null);



       if(accesstoken==null || jwtcreation.validtoken(accesstoken)==false&& jwtcreation.validtoken(refreshtoken)!=false){
           accesstoken=sessionService.accessTokencreation(refreshtoken).orElseThrow(()->
                   new InsufficientAuthenticationException("Access token creation exception"));
       }




          Long id=jwtcreation.getIdByJwtToken(accesstoken);


       User user=userRepo.findById(id).orElseThrow(()->new BadCredentialsException("Not found in db"));

            System.out.println("...............................................asigning"+
                    SecurityContextHolder.getContext().getAuthentication()+"......."+user);
       if(  SecurityContextHolder.getContext().getAuthentication()==null ){
           System.out.println(".................................................asigning"+SecurityContextHolder.getContext().getAuthentication()+"......."+user);
           UsernamePasswordAuthenticationToken userpasstoken=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

           SecurityContextHolder.getContext().setAuthentication(userpasstoken);

       }

   }catch (JwtException | IllegalArgumentException ex) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Invalid JWT", ex)
            );


        }

        filterChain.doFilter(request,response);
    }
}
