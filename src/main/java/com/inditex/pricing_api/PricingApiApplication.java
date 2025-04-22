package com.inditex.pricing_api;

import com.inditex.pricing_api.infrastructure.config.PricingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PricingProperties.class)
public class PricingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingApiApplication.class, args);
	}

}
