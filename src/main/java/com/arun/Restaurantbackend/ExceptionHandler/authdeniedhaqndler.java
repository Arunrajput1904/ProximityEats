package com.arun.Restaurantbackend.ExceptionHandler;

import com.arun.Restaurantbackend.Advice.Apierror;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class authdeniedhaqndler implements AccessDeniedHandler {
     ObjectMapper objectMapper=new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Apierror apierror= Apierror.builder().reason(HttpStatus.FORBIDDEN).status(403).message(accessDeniedException.getMessage()).build();


        response.getWriter().write(objectMapper.writeValueAsString(apierror));
        response.setStatus(403);
    }
}
