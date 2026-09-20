package com.arun.Restaurantbackend.Handler;

import com.arun.Restaurantbackend.Exception.SessionAuthenticationException;
import com.arun.Restaurantbackend.Service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class logouthandler implements LogoutHandler {

    private  final SessionService sessionService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {

        Cookie[] cookies=request.getCookies();
 System.out.println(Arrays.toString(cookies)+"..................................................");
        String refreshtoken= Arrays.stream(cookies).filter(item->item.getName().equals("refreshtoken"))
                .map(item->item.getValue().toString()).findFirst()
                .orElseThrow(()->new InsufficientAuthenticationException("Cookie is not found"));

        try {
            sessionService.Deleterefreshtoken(refreshtoken);
        }catch (SessionAuthenticationException ex){
            throw new SessionAuthenticationException("Illegal refreshtoken");
        }

        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
