package com.ferreusveritas.shavianencoder.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ferreusveritas.shavianencoder")
public class ShavianEncoderApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ShavianEncoderApplication.class, args);
	}
	
}
