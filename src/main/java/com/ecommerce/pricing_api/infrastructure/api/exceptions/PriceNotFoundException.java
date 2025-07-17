package com.ecommerce.pricing_api.infrastructure.api.exceptions;

import java.time.LocalDateTime;

/**
 * Exception thrown when no applicable price is found for a given query.
 * <p>
 * This exception signals a 404 Not Found error in the API and constructs
 * a descriptive message based on productId, brandId, and applicationDate.
 * </p>
 */
public class PriceNotFoundException extends RuntimeException {

    /**
     * Constructs a new PriceNotFoundException with a detailed message.
     *
     * @param productId        the identifier of the product
     * @param brandId          the identifier of the brand
     * @param applicationDate  the date and time for which the price was requested
     */
    public PriceNotFoundException(Long productId, Long brandId, LocalDateTime applicationDate) {
        super(String.format(
                "No applicable price found for productId=%d, brandId=%d on date=%s",
                productId, brandId, applicationDate
        ));
    }
}
