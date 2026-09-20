package com.arun.Restaurantbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableKafka
public class RestaurantbackendApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext=SpringApplication.run(RestaurantbackendApplication.class, args);


	}

}
