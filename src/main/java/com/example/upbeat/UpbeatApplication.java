package com.example.upbeat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = RandomContinentController.class)
public class UpbeatApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpbeatApplication.class, args);
	}

}
