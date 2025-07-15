package com.ecommerce.pricing_api.application.usecases;

import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Defines the contract for retrieving the applicable price of a product
 * for a specific brand at a given date and time.
 */
public interface ApplicablePriceUseCase {

    /**
     * Fetches the price that applies to the specified product and brand
     * at the given application date and time.
     *
     * @param productId       the identifier of the product to price
     * @param brandId         the identifier of the brand
     * @param applicationDate the date and time at which to determine the price
     * @return a {@link Mono} emitting an {@link Optional} containing the {@link ApplicablePrice} if found,
     *         or an empty {@code Optional} if no price applies
     */
    Mono<Optional<ApplicablePrice>> getApplicablePrice(Long productId,
                                                       Long brandId,
                                                       LocalDateTime applicationDate);
}
