package com.arun.Restaurantbackend.ExceptionHandler;

import com.arun.Restaurantbackend.Advice.Apierror;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class authenicationexceptionHandler implements AuthenticationEntryPoint {

    private  final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Apierror apierror=Apierror.builder().status(401).reason(HttpStatus.UNAUTHORIZED).message(authException.getMessage()).build();
        response.getWriter().write(mapper.writeValueAsString(apierror));
        response.setStatus(401);

    }
}
