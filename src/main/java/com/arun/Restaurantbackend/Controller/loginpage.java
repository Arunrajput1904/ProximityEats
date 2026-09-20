package com.arun.Restaurantbackend.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class loginpage {


    @GetMapping("/login")
    public void  redirectpage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CsrfToken token = (CsrfToken)
                request.getAttribute(CsrfToken.class.getName());
        System.out.println(token);
        response.sendRedirect("/login.html");

    }



}
