package com.arun.Restaurantbackend;
import com.arun.Restaurantbackend.Handler.jwtfilterhandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterDisableConfig {

    @Bean
    public FilterRegistrationBean<jwtfilterhandler> jwtFilterRegistration(jwtfilterhandler filter) {
        FilterRegistrationBean<jwtfilterhandler> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}

