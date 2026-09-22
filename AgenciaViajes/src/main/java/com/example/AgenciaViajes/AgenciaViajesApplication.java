package com.example.AgenciaViajes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgenciaViajesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgenciaViajesApplication.class, args);
	}

}
