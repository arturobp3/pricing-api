package com.ecommerce.pricing_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the response payload for an applicable price query.
 * <p>
 * Encapsulates all relevant details about the price that applies to a given product,
 * brand and time period, excluding internal priority information.
 * </p>
 *
 * @param productId the identifier of the product
 * @param brandId   the identifier of the brand
 * @param priceList the price list identifier under which this price was defined
 * @param startDate the start of the time range when this price becomes effective
 * @param endDate   the end of the time range when this price is no longer effective
 * @param price     the monetary value of the price
 * @param currency  the ISO 4217 currency code for the price (e.g. "EUR", "USD")
 */
public record ApplicablePriceResponseDto(
        Long productId,
        Long brandId,
        Long priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {}
