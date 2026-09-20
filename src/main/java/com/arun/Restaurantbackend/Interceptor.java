package com.arun.Restaurantbackend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class Interceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        System.out.println(request.getParameter("User"));
        System.out.println(request.getMethod());
        System.out.println(request.getRequestURI());
        System.out.println(request.getSession());
        System.out.println(Arrays.toString(request.getCookies()));

        if(handler instanceof HandlerMethod method){
            System.out.println(method.getBeanType().getName());
            System.out.println(method.getMethod().getName());
        }
        System.out.println("pre executed");


        return true;
    }

}
