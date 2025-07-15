package com.ecommerce.pricing_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents the price information applicable to a given product and brand
 * for a specific time interval and priority.
 * <p>
 * All fields are wrapped in {@link Optional} to handle potential null values
 * coming from database queries or external systems safely.
 * </p>
 *
 * @param productId  the identifier of the product, or empty if unavailable
 * @param brandId    the identifier of the brand, or empty if unavailable
 * @param priceList  the identifier of the price list applicable, or empty if unavailable
 * @param startDate  the start date and time from which this price is valid, or empty if unavailable
 * @param endDate    the end date and time until which this price is valid, or empty if unavailable
 * @param price      the price amount, or empty if unavailable
 * @param currency   the currency code of the price (e.g., "EUR", "USD"), or empty if unavailable
 * @param priority   the priority of this price when overlapping intervals occur, or empty if unavailable
 */
public record ApplicablePrice(
        Optional<Long> productId,
        Optional<Long> brandId,
        Optional<Long> priceList,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Optional<BigDecimal> price,
        Optional<String> currency,
        Optional<Long> priority
) {}
