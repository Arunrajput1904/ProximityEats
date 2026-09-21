package com.arun.Restaurantbackend.Controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Hidden
@RestController
@Tag(name = "Login api")
public class loginpage {


    @GetMapping("/login")
    public void  redirectpage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CsrfToken token = (CsrfToken)
                request.getAttribute(CsrfToken.class.getName());
        System.out.println(token);
        response.sendRedirect("/login.html");

    }



}
