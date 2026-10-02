package com.ganpat.spendlyticsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpendlyticsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpendlyticsBackendApplication.class, args);
	}

}
