package com.example.webappproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enables background cleanup tasks
@EnableJpaRepositories(considerNestedRepositories = true) // Tells Spring to find your repos!
public class WebappprojectApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebappprojectApplication.class, args);
	}
}