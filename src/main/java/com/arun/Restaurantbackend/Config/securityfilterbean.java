package com.arun.Restaurantbackend.Config;


import com.arun.Restaurantbackend.ExceptionHandler.authdeniedhaqndler;
import com.arun.Restaurantbackend.ExceptionHandler.authenicationexceptionHandler;
import com.arun.Restaurantbackend.Handler.Successsessionhandler;
import com.arun.Restaurantbackend.Handler.failurehandler;
import com.arun.Restaurantbackend.Handler.jwtfilterhandler;
import com.arun.Restaurantbackend.Handler.logouthandler;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class securityfilterbean {

    private  final Successsessionhandler successsessionhandler;
private  final authenicationexceptionHandler authenicationexception;
private final failurehandler failurehandler;
private final jwtfilterhandler jwtfilterhandler;
private final authdeniedhaqndler authdeniedhaqndler;
private final logouthandler logouthandler;
    List<String> strings= List.of("/favicon.ico","/css/app.css","/default-ui.css","/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html");



    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChain2(HttpSecurity httpSecurity) throws  Exception{
        return         httpSecurity
                .csrf(csrf->csrf.disable())
                .securityMatcher("/api/**")
                .authorizeHttpRequests(
                        auth->auth.requestMatchers("/error").
                                permitAll().anyRequest().authenticated()
                )
                .addFilterBefore(jwtfilterhandler, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtfilterhandler,UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception->
                        exception.authenticationEntryPoint(authenicationexception).
                                accessDeniedHandler(authdeniedhaqndler))
                .sessionManagement(session->session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout->logout.addLogoutHandler(logouthandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain1(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
//                .csrf()
                .securityMatcher(request -> !request.getRequestURI().equals("/api/**"))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/logout","/auth/signup"
                                                ,"/auth/forget","/auth/sendEmail","/emailsend.html"
                                                ,"/auth/**","/paydone/**",
                                                "/templates/**","/signup.html","/manager.html",
                                                "/auth/change-password","/actuator/health"
                                        ,"/paydone/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html","/docs"
                                                ,"/updatepassword.html","/login.html","/login","/topic/**","/loginsuccess").permitAll()
                                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                        .requestMatchers(strings.get(0),strings.get(1),strings.get(2),strings.get(3),strings.get(4),strings.get(5)).permitAll()
                                        .anyRequest().authenticated()
                )
                .csrf(csrf->csrf.disable())
                .requestCache(cache->cache.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form->form.
                        loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successsessionhandler)
                        .failureHandler(failurehandler))
                .exceptionHandling(exception->
                        exception
                                .authenticationEntryPoint(authenicationexception)
                                .accessDeniedHandler(authdeniedhaqndler))
                .oauth2Login(auth->auth.
                        loginPage("/login").
                        successHandler(successsessionhandler))
                .logout(logout->logout
                        .addLogoutHandler(logouthandler));


        return httpSecurity.build();
    }



}
