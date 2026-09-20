package com.arun.Restaurantbackend.Config;

import com.arun.Restaurantbackend.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RegistryIntercept implements WebMvcConfigurer {
private final Interceptor interceptor;


    public RegistryIntercept(Interceptor interceptor) {
        this.interceptor = interceptor;
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**")
                .addPathPatterns("/login/**");
    }




}
