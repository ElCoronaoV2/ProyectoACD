package com.restaurant.tec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RestaurantecApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantecApplication.class, args);
	}

}
