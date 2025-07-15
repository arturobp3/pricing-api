package com.ecommerce.pricing_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main entry point for the Pricing API application.
 * <p>
 * This class bootstraps the Spring Boot context and starts the embedded server.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class PricingApiApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args runtime arguments (e.g., --server.port=8080)
	 */
	public static void main(String[] args) {
		SpringApplication.run(PricingApiApplication.class, args);
	}

}
