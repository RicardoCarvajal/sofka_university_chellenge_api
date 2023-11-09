package com.intelix.challenge.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Aplicación de facturación y reportes", version = "0.0.1", description = "Documentation APIs v0.0.1"))
public class SofkaChallengeApiApplication { 

	public static void main(String[] args) {
		SpringApplication.run(SofkaChallengeApiApplication.class, args);
	}

}
