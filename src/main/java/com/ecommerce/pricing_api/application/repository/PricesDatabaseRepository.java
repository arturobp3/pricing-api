package com.ecommerce.pricing_api.application.repository;

import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import reactor.core.publisher.Flux;

/**
 * Repository interface for retrieving price data from the underlying database.
 * <p>
 * Provides a reactive API to stream all {@link ApplicablePrice} entries
 * associated with a given product and brand.
 * </p>
 */
public interface PricesDatabaseRepository {

    /**
     * Finds all prices in the database for the specified product and brand.
     * <p>
     * The returned {@link Flux} emits zero or more {@link ApplicablePrice}
     * instances, each representing a price record with its validity period,
     * priority, currency, and other details.
     * </p>
     *
     * @param productId the identifier of the product
     * @param brandId   the identifier of the brand
     * @return a {@link Flux} that emits all matching {@link ApplicablePrice} records,
     *         or completes empty if none are found
     */
    Flux<ApplicablePrice> findAllByProductAndBrand(Long productId, Long brandId);
}
