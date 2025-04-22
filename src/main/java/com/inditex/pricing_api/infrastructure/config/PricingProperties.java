package com.inditex.pricing_api.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.ZoneId;

@Data
@Configuration
@ConfigurationProperties(prefix = "pricing")
public class PricingProperties {

    private String defaultCurrency;
}